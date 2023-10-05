package com.example.webapp.controller;

import com.example.webapp.model.Assignment;
import com.example.webapp.model.User;
import com.example.webapp.service.AssignmentService;
import com.example.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/assignment")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment) {

        if (assignment.getPoints() < 0 || assignment.getPoints() > 10) {
            // HTTP 400 BAD REQUEST
            return ResponseEntity.badRequest().body("Creation Fail. Points Must Be Between 0 and 10.");
        }

        return ResponseEntity.ok(assignmentService.createAssignment(assignment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable Long id, @RequestBody Assignment assignment) {

        if (assignment.getPoints() < 0 || assignment.getPoints() > 10) {
            // HTTP 400 BAD REQUEST
            return ResponseEntity.badRequest().body("Update Fail. Points Must Be Between 0 and 10.");
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            User currentUser = userService.findByEmail(username);

            return ResponseEntity.ok(assignmentService.updateAssignment(id, assignment, currentUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            User currentUser = userService.findByEmail(username);
            assignmentService.deleteAssignment(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }

    }

}
