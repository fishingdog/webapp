package com.example.webapp.healthz;


import jakarta.persistence.EntityManagerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/healthz")
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @GetMapping
    public ResponseEntity<Void> checkHealth() {
        try (Connection connection = dataSource.getConnection()) {
            dataSource.setLoginTimeout(3);
            if (connection.isValid(1000)) {
                return ResponseEntity.ok()
                        .header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .build();
            } else {
                throw new Exception("Invalid connection");
            }
        } catch (Exception e) {
            logger.error("Health check failed", e); // This will log to CloudWatch
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", e);
        }
    }
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void methodNotAllowed() {
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Void> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .build();
    }
}


