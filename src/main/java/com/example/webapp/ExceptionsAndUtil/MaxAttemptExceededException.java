package com.example.webapp.ExceptionsAndUtil;

public class MaxAttemptExceededException extends Exception {
    public MaxAttemptExceededException(String message) {
        super(message);
    }
}

