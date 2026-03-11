package com.cyberassure.cyberassureproject.exception;

/**
 * Thrown when an underwriter or claims officer submits an unrecognized decision value.
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidDecisionException extends RuntimeException {
    public InvalidDecisionException(String message) {
        super(message);
    }
}
