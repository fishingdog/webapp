package com.example.webapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User creator;

    private String title;
    private String description;
    private int points;

    @Column(updatable = false)
    private LocalDateTime assignmentCreated;
    private LocalDateTime assignmentUpdated;

    public Long getId() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPoints() {
        return points;
    }

    public LocalDateTime getAssignmentCreated() {
        return assignmentCreated;
    }

    public LocalDateTime getAssignmentUpdated() {
        return assignmentUpdated;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setAssignmentCreated(LocalDateTime assignmentCreated) {
        this.assignmentCreated = assignmentCreated;
    }

    public void setAssignmentUpdated(LocalDateTime assignmentUpdated) {
        this.assignmentUpdated = assignmentUpdated;
    }
}
