package com.cyberassure.cyberassureproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ErrorResponse.builder()
                                                .message(ex.getMessage())
                                                .status(HttpStatus.CONFLICT.value())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ErrorResponse.builder()
                                                .message(ex.getMessage())
                                                .status(HttpStatus.UNAUTHORIZED.value())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ErrorResponse.builder()
                                                .message(ex.getMessage())
                                                .status(HttpStatus.NOT_FOUND.value())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
                String errorMessage = ex.getBindingResult().getFieldError() != null
                                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                                : "Validation failed";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.builder()
                                                .message(errorMessage)
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
                ex.printStackTrace(); // Debugging 400 Bad Request masking
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.builder()
                                                .message(ex.getMessage())
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ErrorResponse.builder()
                                                .message("Something went wrong: " + ex.getMessage())
                                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                .timestamp(LocalDateTime.now())
                                                .build());
        }
}