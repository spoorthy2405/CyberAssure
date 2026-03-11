package com.cyberassure.cyberassureproject.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    /** Short machine-readable error category label (e.g. "NOT_FOUND", "UNAUTHORIZED") */
    private String error;

    /** Human-readable message explaining what went wrong */
    private String message;

    /** HTTP status code (e.g. 404, 403, 422) */
    private int status;

    /** When the error occurred */
    private LocalDateTime timestamp;
}