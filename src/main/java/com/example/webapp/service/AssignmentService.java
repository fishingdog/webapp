package com.example.webapp.service;

import com.example.webapp.model.Assignment;
import com.example.webapp.model.User;
import com.example.webapp.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    public Assignment createAssignment(Assignment assignment){

        assignment.setAssignmentCreated(LocalDateTime.now());
        assignment.setAssignmentUpdated(LocalDateTime.now());

        return assignmentRepository.save(assignment);
    }

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    public Assignment updateAssignment(Long id, Assignment assignment, User currentUser) throws IllegalAccessError, IllegalAccessException {

        Assignment existingAssignment = assignmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Assignment Not Found."));

        if (!existingAssignment.getCreator().equals(currentUser)) {
            throw new IllegalAccessException("Cannot Update. Permission Denied.");
        }

        existingAssignment.setAssignmentUpdated(LocalDateTime.now());
        existingAssignment.setTitle(assignment.getTitle());
        existingAssignment.setDescription(assignment.getDescription());
        existingAssignment.setPoints(assignment.getPoints());
        return assignmentRepository.save(existingAssignment);
    }

    public void deleteAssignment(Long id, User currentUser) throws IllegalAccessException {
        Assignment existingAssignment = assignmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Assignment Not Found."));

        if (!existingAssignment.getCreator().equals(currentUser)) {
            throw new IllegalAccessException("Cannot Delete. Permission Denied.");
        }

        assignmentRepository.delete(existingAssignment);
    }



}
