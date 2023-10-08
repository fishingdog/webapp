package com.example.webapp.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Assignment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;


    @ManyToOne
    private User creator;


    private String name;
    private Date deadline;
    private int points;
    private int num_of_attempts;


    @Column(updatable = false)
    private LocalDateTime assignmentCreated;
    private LocalDateTime assignmentUpdated;

}
