package com.example.webapp.repository;

import com.example.webapp.model.Submission;
import com.example.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    Optional<Submission> findById(UUID id);
    List<Submission> findByAssignmentId(UUID assignmentId);



}
