package com.example.webapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID assignmentId;

    @ManyToOne
    private User submitter;

    @Column(nullable = false)
    private String submissionUrl;

    @Column(nullable = false)
    private int numberOfAttempts;

    @Column(updatable = false)
    private LocalDateTime submissionDate;
    private LocalDateTime submissionRetried;
}
