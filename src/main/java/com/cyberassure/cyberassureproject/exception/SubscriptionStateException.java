package com.cyberassure.cyberassureproject.exception;

/**
 * Thrown when a subscription operation is attempted in the wrong state.
 * E.g., trying to pay for a subscription that is already ACTIVE or REJECTED.
 * Maps to HTTP 409 Conflict.
 */
public class SubscriptionStateException extends RuntimeException {
    public SubscriptionStateException(String message) {
        super(message);
    }
}
