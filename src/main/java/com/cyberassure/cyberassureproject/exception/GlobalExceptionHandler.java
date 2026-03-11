package com.cyberassure.cyberassureproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST controllers.
 *
 * Every exception thrown anywhere in the application is caught here and
 * converted into a clean, structured JSON response instead of a raw stack trace.
 *
 * Response format:
 * {
 *   "error":     "NOT_FOUND",
 *   "message":   "No claim found with ID: 42",
 *   "status":    404,
 *   "timestamp": "2026-03-10T14:00:00"
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─────────────────────────────────────────────────────
    // 400 BAD REQUEST
    // ─────────────────────────────────────────────────────

    /**
     * Triggered when @Valid or @Validated annotations fail on a request body.
     * Collects ALL field-level validation errors into one readable string.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> "'" + fe.getField() + "' " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
                "Request validation failed: " + details);
    }

    /**
     * Thrown when an underwriter or claims officer submits a decision value
     * that isn't in the allowed set (e.g. "MAYBE" instead of "APPROVED"/"REJECTED").
     */
    @ExceptionHandler(InvalidDecisionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDecision(InvalidDecisionException ex) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_DECISION", ex.getMessage());
    }

    // ─────────────────────────────────────────────────────
    // 401 UNAUTHORIZED  (wrong credentials / bad token)
    // ─────────────────────────────────────────────────────

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                "Email or password is incorrect. Please try again.");
    }

    // ─────────────────────────────────────────────────────
    // 403 FORBIDDEN  (authenticated but not allowed)
    // ─────────────────────────────────────────────────────

    /**
     * Thrown when a user tries to act on a resource they don't own
     * (e.g. filing a claim on someone else's policy).
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", ex.getMessage());
    }

    /**
     * Spring Security throws this when @PreAuthorize fails (wrong role).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "You do not have permission to perform this action.");
    }

    // ─────────────────────────────────────────────────────
    // 404 NOT FOUND
    // ─────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    // ─────────────────────────────────────────────────────
    // 409 CONFLICT  (state/domain rule violations)
    // ─────────────────────────────────────────────────────

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", ex.getMessage());
    }

    /**
     * Thrown when a claim is filed against a policy that is not in ACTIVE status.
     */
    @ExceptionHandler(PolicyNotActiveException.class)
    public ResponseEntity<ErrorResponse> handlePolicyNotActive(PolicyNotActiveException ex) {
        return build(HttpStatus.CONFLICT, "POLICY_NOT_ACTIVE", ex.getMessage());
    }

    /**
     * Thrown when a subscription operation is attempted in the wrong status
     * (e.g. paying for an already ACTIVE subscription).
     */
    @ExceptionHandler(SubscriptionStateException.class)
    public ResponseEntity<ErrorResponse> handleSubscriptionState(SubscriptionStateException ex) {
        return build(HttpStatus.CONFLICT, "INVALID_SUBSCRIPTION_STATE", ex.getMessage());
    }

    // ─────────────────────────────────────────────────────
    // 422 UNPROCESSABLE ENTITY  (business rule failures)
    // ─────────────────────────────────────────────────────

    /**
     * Thrown when a claim fails business validation:
     * - Claim amount exceeds the policy's coverage limit
     * - Policy has expired
     * - Claim is already finalized (can't re-review SETTLED claims)
     */
    @ExceptionHandler(ClaimValidationException.class)
    public ResponseEntity<ErrorResponse> handleClaimValidation(ClaimValidationException ex) {
        return build(HttpStatus.valueOf(422), "CLAIM_VALIDATION_FAILED", ex.getMessage());
    }

    // ─────────────────────────────────────────────────────
    // 500 INTERNAL SERVER ERROR  (unexpected / unhandled)
    // ─────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ex.printStackTrace(); // log full trace for server-side debugging
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later.");
    }

    // ─────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .error(error)
                        .message(message)
                        .status(status.value())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}