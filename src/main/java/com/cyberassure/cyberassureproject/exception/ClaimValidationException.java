package com.cyberassure.cyberassureproject.exception;

/**
 * Thrown when a claim fails business validation rules:
 * - Claim amount exceeds coverage limit
 * - Policy has expired
 * - Claim is already finalized
 * Maps to HTTP 422 Unprocessable Entity.
 */
public class ClaimValidationException extends RuntimeException {
    public ClaimValidationException(String message) {
        super(message);
    }
}
