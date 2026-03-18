package com.cyberassure.cyberassureproject.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidation_ShouldReturnBadRequestWithDetails() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("objectName", "field1", "must not be blank"),
                new FieldError("objectName", "field2", "must be a valid email")
        ));

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_FAILED", response.getBody().getError());
        assertEquals("Request validation failed: 'field1' must not be blank, 'field2' must be a valid email", response.getBody().getMessage());
    }

    @Test
    void handleInvalidDecision_ShouldReturnBadRequest() {
        InvalidDecisionException ex = new InvalidDecisionException("Invalid decision: MAYBE");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidDecision(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_DECISION", response.getBody().getError());
        assertEquals("Invalid decision: MAYBE", response.getBody().getMessage());
    }

    @Test
    void handleInvalidCredentials_ShouldReturnUnauthorized() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Custom invalid credentials message");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidCredentials(ex);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getError());
        assertEquals("Custom invalid credentials message", response.getBody().getMessage());
    }

    @Test
    void handleBadCredentials_ShouldReturnUnauthorizedWithDefaultMessage() {
        BadCredentialsException ex = new BadCredentialsException("Spring message");
        ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(ex);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Email or password is incorrect. Please try again.", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorizedAccess_ShouldReturnForbidden() {
        UnauthorizedAccessException ex = new UnauthorizedAccessException("Not your claim");
        ResponseEntity<ErrorResponse> response = handler.handleUnauthorizedAccess(ex);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("ACCESS_DENIED", response.getBody().getError());
    }

    @Test
    void handleAccessDenied_ShouldReturnForbiddenWithDefaultMessage() {
        AccessDeniedException ex = new AccessDeniedException("Spring message");
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("ACCESS_DENIED", response.getBody().getError());
        assertEquals("You do not have permission to perform this action.", response.getBody().getMessage());
    }

    @Test
    void handleNotFound_ShouldReturnNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found");
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("NOT_FOUND", response.getBody().getError());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleEmailExists_ShouldReturnConflict() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Email exists");
        ResponseEntity<ErrorResponse> response = handler.handleEmailExists(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("EMAIL_ALREADY_EXISTS", response.getBody().getError());
    }

    @Test
    void handlePolicyNotActive_ShouldReturnConflict() {
        PolicyNotActiveException ex = new PolicyNotActiveException("Policy is expired");
        ResponseEntity<ErrorResponse> response = handler.handlePolicyNotActive(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("POLICY_NOT_ACTIVE", response.getBody().getError());
    }

    @Test
    void handleSubscriptionState_ShouldReturnConflict() {
        SubscriptionStateException ex = new SubscriptionStateException("Invalid state");
        ResponseEntity<ErrorResponse> response = handler.handleSubscriptionState(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("INVALID_SUBSCRIPTION_STATE", response.getBody().getError());
    }

    @Test
    void handleClaimValidation_ShouldReturnUnprocessableEntity() {
        ClaimValidationException ex = new ClaimValidationException("Amount exceeds limit");
        ResponseEntity<ErrorResponse> response = handler.handleClaimValidation(ex);
        assertEquals(HttpStatus.valueOf(422), response.getStatusCode());
        assertEquals("CLAIM_VALIDATION_FAILED", response.getBody().getError());
    }

    @Test
    void handleGeneric_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Some generic error");
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_ERROR", response.getBody().getError());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
    }
}
