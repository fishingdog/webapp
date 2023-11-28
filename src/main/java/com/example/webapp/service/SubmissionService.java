package com.example.webapp.service;

import com.example.webapp.ExceptionsAndUtil.MaxAttemptExceededException;
import com.example.webapp.ExceptionsAndUtil.PassDeadlineException;
import com.example.webapp.model.Assignment;
import com.example.webapp.model.Submission;
import com.example.webapp.repository.AssignmentRepository;
import com.example.webapp.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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


    public Optional<Submission> getSubmissionById(UUID id) {
        return submissionRepository.findByAssignmentId(id);
    }


    public Submission retrySubmission(Submission submission) {

        Submission existingSubmission = submissionRepository.findByAssignmentId(submission.getAssignmentId()).orElseThrow(() -> new IllegalArgumentException("Submission Not Found."));

        existingSubmission.setSubmissionRetried(LocalDateTime.now());
        existingSubmission.setSubmissionUrl(submission.getSubmissionUrl());
        existingSubmission.setNumberOfAttempts(existingSubmission.getNumberOfAttempts()+1);

        return submissionRepository.save(existingSubmission);

    }
}
