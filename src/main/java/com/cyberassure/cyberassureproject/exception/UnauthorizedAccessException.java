package com.cyberassure.cyberassureproject.exception;

/**
 * Thrown when a user attempts to access or modify a resource they do not own.
 * Maps to HTTP 403 Forbidden.
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
