package com.cyberassure.cyberassureproject.exception;

/**
 * Thrown when a customer tries to file a claim against a policy that is not ACTIVE.
 * Maps to HTTP 409 Conflict.
 */
public class PolicyNotActiveException extends RuntimeException {
    public PolicyNotActiveException(String message) {
        super(message);
    }
}
