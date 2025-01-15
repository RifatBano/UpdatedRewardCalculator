package com.infy.RewardPointCalculator.exception;

public class ResourceNotFoundException extends RuntimeException {

    // Constructor that takes a custom message
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    // Constructor that takes a message and a cause (optional)
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
