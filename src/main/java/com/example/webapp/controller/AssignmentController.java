package com.example.webapp.controller;

import com.example.webapp.model.Assignment;
import com.example.webapp.model.User;
import com.example.webapp.service.AssignmentService;
import com.example.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/v1/assignment")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
//        }


        if (assignment.getPoints() < 0 || assignment.getPoints() > 10) {
            // HTTP 400 BAD REQUEST
            return ResponseEntity.badRequest().body("Creation Fail. Points Must Be Between 0 and 10.");
        }


        if (assignment.getNum_of_attempts() < 0 || assignment.getNum_of_attempts() > 100) {
            // HTTP 400 BAD REQUEST
            return ResponseEntity.badRequest().body("Creation Fail. Allowed Attempts Must Be Between 0 and 100.");
        }


        String username = (String) authentication.getPrincipal();


        // Retrieve the corresponding User entity from the database
        User currentUser = userService.findByEmail(username);

        // Set the retrieved User entity as the creator of the Assignment
        assignment.setCreator(currentUser);

        return new ResponseEntity(assignmentService.createAssignment(assignment), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignment(@PathVariable UUID id) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }


            String username = (String) authentication.getPrincipal();


            User currentUser = userService.findByEmail(username);

            return ResponseEntity.ok(assignmentService.getAssignmentById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable UUID id, @RequestBody Assignment assignment) {

        if (assignment.getPoints() < 0 || assignment.getPoints() > 10) {
            // HTTP 400 BAD REQUEST
            return ResponseEntity.badRequest().body("Update Fail. Points Must Be Between 0 and 10.");
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String username = (String) authentication.getPrincipal();


            User currentUser = userService.findByEmail(username);


            return ResponseEntity.ok(assignmentService.updateAssignment(id, assignment, currentUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PatchMapping("/{name}")
    public ResponseEntity<?> handlePatch() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("PATCH is not allowed for this resource.");
    }

    @PatchMapping
    public ResponseEntity<?> handlePatchBase() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("PATCH is not allowed for this resource.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable UUID id) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }

            String username = (String) authentication.getPrincipal();


            User currentUser = userService.findByEmail(username);
            assignmentService.deleteAssignment(id, currentUser);

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalAccessException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

}
