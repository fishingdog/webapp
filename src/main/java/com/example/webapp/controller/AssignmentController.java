package com.example.webapp.controller;

import com.example.webapp.ExceptionsAndUtil.MaxAttemptExceededException;
import com.example.webapp.ExceptionsAndUtil.PassDeadlineException;
import com.example.webapp.model.Assignment;
import com.example.webapp.model.Submission;
import com.example.webapp.model.User;
import com.example.webapp.service.AssignmentService;
import com.example.webapp.service.SubmissionService;
import com.example.webapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.Topic;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Both assignment and Submission Controllers are here
@RestController
@RequestMapping("/v1/assignment")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);
    @Autowired
    private MeterRegistry meterRegistry;
    private Counter assignmentCounterPost;
    private Counter assignmentCounterGet;
    private Counter assignmentCounterPut;
    private Counter assignmentCounterDelete;
    @PostConstruct
    public void init() {
        // Initialize the counter
        assignmentCounterPost = Counter.builder("endpoint.v1/assignment.PostCalls")
                .description("Number of Post calls to the v1/assignment endpoint")
                .register(meterRegistry);
        assignmentCounterGet = Counter.builder("endpoint.v1/assignment.GetCalls")
                .description("Number of Get calls to the v1/assignment endpoint")
                .register(meterRegistry);
        assignmentCounterPut = Counter.builder("endpoint.v1/assignment.PutCalls")
                .description("Number of Put calls to the v1/assignment endpoint")
                .register(meterRegistry);
        assignmentCounterDelete = Counter.builder("endpoint.v1/assignment.DeleteCalls")
                .description("Number of Delete calls to the v1/assignment endpoint")
                .register(meterRegistry);
    }
    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment) {
        assignmentCounterPost.increment();
        logger.info("Attempting to create a new assignment");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        // Validate assignment name range HTTP 400
        if (assignment.getName() == null) {
            String nameError = "Creation Fail. Assignments must have a name.";
            logger.error(nameError);
            return ResponseEntity.badRequest().body(nameError);
        }

        // Validate points range HTTP 400
        if (assignment.getPoints() < 0 || assignment.getPoints() > 100) {
            String pointsError = "Creation Fail. Points must be between 0 and 100.";
            logger.error(pointsError + " Provided: {}", assignment.getPoints());
            return ResponseEntity.badRequest().body(pointsError);
        }

        // Validate number of attempts HTTP 400
        if (assignment.getNum_of_attempts() < 1 || assignment.getNum_of_attempts() > 100) {
            String attemptsError = "Creation Fail. num_of_attempts must be between 0 and 100.";
            logger.error(attemptsError + " Provided: {}", assignment.getNum_of_attempts());
            return ResponseEntity.badRequest().body(attemptsError);
        }

        // Validate deadline HTTP 400
        if (assignment.getDeadline() == null) {
            String deadlineError = "Creation Fail. Assignments must have a deadline.";
            logger.error(deadlineError);
            return ResponseEntity.badRequest().body(deadlineError);
        }

        try {
            String username = (String) authentication.getPrincipal();

            // Retrieve the corresponding User entity from the database
            User currentUser = userService.findByEmail(username);

            // Set the retrieved User entity as the creator of the Assignment
            assignment.setCreator(currentUser);

            // Assume the creation logic is successful
            Assignment savedAssignment = assignmentService.createAssignment(assignment);
            logger.info("Assignment created successfully with id: {}", savedAssignment.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAssignment);
        } catch (Exception e) {
            logger.error("Error during assignment creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during assignment creation");
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignment(@PathVariable UUID id) {
        assignmentCounterGet.increment();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                logger.warn("Unauthorized access attempt to get assignment with id: {}", id);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            String username = (String) authentication.getPrincipal();

            User currentUser = userService.findByEmail(username);
            if (currentUser == null) {
                logger.warn("User with username: {} not found", username);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Optional<?> assignment = assignmentService.getAssignmentById(id);
            if (assignment.isEmpty()) {
                logger.warn("Assignment with id: {} not found", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(assignment);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching assignment with id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable UUID id, @RequestBody Assignment assignment) {

        assignmentCounterPut.increment();
        logger.info("Attempting to update assignment with id: {}", id);

        if (assignment.getPoints() < 0 || assignment.getPoints() > 10) {
            // HTTP 400 BAD REQUEST
            logger.warn("Update failed. Invalid points: {}", assignment.getPoints());
            return ResponseEntity.badRequest().body("Update Fail. Points Must Be Between 0 and 10.");
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String username = (String) authentication.getPrincipal();
            User currentUser = userService.findByEmail(username);

            Assignment updatedAssignment = assignmentService.updateAssignment(id, assignment, currentUser);
            logger.info("Assignment with id: {} updated successfully by user: {}", id, username);
            return ResponseEntity.ok(updatedAssignment);
        } catch (IllegalArgumentException e) {
            logger.error("No content found for the given assignment id: {}", id, e);
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            logger.error("Access violation while updating assignment with id: {}", id, e);
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PatchMapping("/{name}")
    public ResponseEntity<?> handlePatch() {
        logger.info("PATCH request not allowed.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("PATCH is not allowed for this resource.");
    }

    @PatchMapping
    public ResponseEntity<?> handlePatchBase() {
        logger.info("PATCH request not allowed.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("PATCH is not allowed for this resource.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable UUID id) {
        assignmentCounterDelete.increment();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                logger.warn("Unauthorized attempt to delete assignment with id {}", id);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            String username = (String) authentication.getPrincipal();
            logger.info("User {} is attempting to delete assignment with id {}", username, id);

            User currentUser = userService.findByEmail(username);

            assignmentService.deleteAssignment(id, currentUser);
            logger.info("Assignment with id {} deleted successfully by user {}", id, username);

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Attempted to delete non-existing assignment with id {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (IllegalAccessException e) {
            logger.error("User attempted to delete assignment without permission, assignment id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/submission")
    public ResponseEntity<?> submitAssignment(@PathVariable UUID id, @RequestBody Submission submission) {
        logger.info("Attempting to submit an assignment with id: {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        // validate if there is id
        if (id == null) {
            logger.error("Assignment ID must not be null.");
            return ResponseEntity.badRequest().body("Assignment ID must not be null.");
        }

        // find assignment or 404
        try {
            Assignment existingAssignment = assignmentService.getAssignmentById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Assignment Not Found"));

            // check if passed deadline. Reject if passed.
            if (existingAssignment.getDeadline().isBefore(LocalDateTime.now())) {
                throw new PassDeadlineException("Submission rejected. Passed deadline.");
            }
        } catch (PassDeadlineException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        // Validate url HTTP 400
        if (submission.getSubmissionUrl() == null) {
            String urlError = "Creation Fail. Must specify a submission url.";
            logger.error(urlError);
            return ResponseEntity.badRequest().body(urlError);
        }

        // submission uses corresponding assignment id as its id
        Optional<Submission> fetchSubmission = submissionService.getSubmissionById(id);
        if (fetchSubmission.isEmpty()) {
            logger.info("creating a new submission for assignment id {}", id);

            try {
                String username = (String) authentication.getPrincipal();
                User currentUser = userService.findByEmail(username);

                submission.setSubmitter(currentUser);
                submission.setAssignmentId(id);
                Submission savedSubmission = submissionService.createSubmission(submission);
                logger.info("Submission created successfully with id: {}", id);

                publishSNS(savedSubmission);
                logger.info("Submission publishing to SNS");

                return ResponseEntity.status(HttpStatus.CREATED).body(savedSubmission);
            } catch (Exception e) {
                logger.error("Error during submission creation", e);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Error during submission creation");
            }
        } else {
            try {
                Assignment existingAssignment = assignmentService.getAssignmentById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Assignment Not Found"));
                Submission existingSubmission = submissionService.getSubmissionById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
                //count how many times retried. Reject if more than allowed attempts
                int maxAttempts = existingAssignment.getNum_of_attempts();
                if (existingSubmission.getNumberOfAttempts() >= maxAttempts) {
                    throw new MaxAttemptExceededException("Retry rejected. Max number of attempts reached.");
                }

                submission.setAssignmentId(id);
                Submission savedSubmission = submissionService.retrySubmission(submission);
                logger.info("Resubmission successfully with id: {}", id);

                publishSNS(savedSubmission);
                logger.info("Submission publishing to SNS");

                return ResponseEntity.status(HttpStatus.CREATED).body(savedSubmission);
            } catch (IllegalArgumentException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (MaxAttemptExceededException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                logger.error("Error during resubmission", e);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Error during resubmission");
            }
        }

    }

    private static String findTopicArnByName(SnsClient snsClient, String topicName) {
        String nextToken = null;
        do {
            String finalNextToken = nextToken;
            ListTopicsResponse listTopicsResponse = snsClient.listTopics(builder -> builder.nextToken(finalNextToken));
            Optional<Topic> matchingTopic = listTopicsResponse.topics().stream()
                    .filter(topic -> topic.topicArn().endsWith(":" + topicName))
                    .findFirst();

            if (matchingTopic.isPresent()) {
                return matchingTopic.get().topicArn();
            }

            nextToken = listTopicsResponse.nextToken();
        } while (nextToken != null);

        throw new IllegalStateException("Topic with name " + topicName + " not found.");
    }

    public static void publishSNS(Submission submission) {

        // Set up the SNS client
        SnsClient snsClient = SnsClient.builder()
                .region(Region.of("us-west-2")) // Change to your region
                .build();

        // The ARN of the SNS topic you want to publish to
        String topicName = "lambdaTopic";
        String topicArn = findTopicArnByName(snsClient, topicName);
        logger.info("topicArn: {}", topicArn);

        // Construct the message as a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson = "";
        try {
            messageJson = objectMapper.writeValueAsString(Map.of(
                    "assignmentId", submission.getAssignmentId().toString(),
                    "submitterEmail", submission.getSubmitter().getEmail(),
                    "submissionAttempt", submission.getNumberOfAttempts(),
                    "submissionUrl", submission.getSubmissionUrl()
            ));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Failed to construct message JSON", e);
            // Handle JSON construction error
        }
        // Create and publish the SNS message
        try {
            PublishRequest publishRequest = PublishRequest.builder()
                    .message(messageJson) // Message body
                    .topicArn(topicArn)
                    .build();

            snsClient.publish(publishRequest);
            logger.info("Submission URL sent to SNS topic");
        } catch (Exception e) {
            logger.error("Failed to send submission URL to SNS topic", e);
            // Decide how you want to handle this failure. For now, let's just log it.
        } finally {
            snsClient.close(); // Close the SNS client
        }
    }
}
