package com.example.webapp.repository;

import com.example.webapp.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

//    Optional<Submission> findById(UUID id);
    Optional<Submission> findByAssignmentId(UUID assignmentId);

}
