package com.example.webapp.service;

import com.example.webapp.ExceptionsAndUtil.MaxAttemptExceededException;
import com.example.webapp.ExceptionsAndUtil.PassDeadlineException;
import com.example.webapp.model.Assignment;
import com.example.webapp.model.Submission;
import com.example.webapp.model.User;
import com.example.webapp.repository.AssignmentRepository;
import com.example.webapp.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
//    @Autowired
//    private AssignmentRepository assignmentRepository;

    public Submission createSubmission(Submission submission){
        submission.setNumberOfAttempts(1);
        submission.setSubmissionDate(LocalDateTime.now());
        submission.setSubmissionRetried(LocalDateTime.now());
        return submissionRepository.save(submission);
    }


    public List<Submission> getSubmissionById(UUID id) {
        return submissionRepository.findByAssignmentId(id);
    }

    public Optional<Submission> getSubmissionBySubmitter(List<Submission> submissions, User submitter) {
        return submissions.stream()
                .filter(submission -> submission.getSubmitter() != null && submission.getSubmitter().equals(submitter))
                .findFirst();
    }


    public Submission retrySubmission(Submission submission) {

        submission.setSubmissionRetried(LocalDateTime.now());
        submission.setSubmissionDate(LocalDateTime.now());
//        submission.setSubmissionUrl(submission.getSubmissionUrl());
        submission.setNumberOfAttempts(submission.getNumberOfAttempts()+1);

        return submissionRepository.save(submission);

    }
}
