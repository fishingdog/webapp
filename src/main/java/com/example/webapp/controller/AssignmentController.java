package com.example.webapp.controller;

import com.example.webapp.model.Assignment;
import com.example.webapp.model.User;
import com.example.webapp.service.AssignmentService;
import com.example.webapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment) {

        logger.info("Attempting to create a new assignment");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        // Validate points range HTTP 400
        if (assignment.getPoints() < 0 || assignment.getPoints() > 10) {
            String pointsError = "Creation Fail. Points must be between 0 and 10.";
            logger.error(pointsError + " Provided: {}", assignment.getPoints());
            return ResponseEntity.badRequest().body(pointsError);
        }

        // Validate number of attempts HTTP 400
        if (assignment.getNum_of_attempts() < 0 || assignment.getNum_of_attempts() > 100) {
            String attemptsError = "Creation Fail. Number of attempts must be between 0 and 100.";
            logger.error(attemptsError + " Provided: {}", assignment.getNum_of_attempts());
            return ResponseEntity.badRequest().body(attemptsError);
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

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                logger.warn("Unauthorized access attempt to get assignment with id: {}", id);
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
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

}
