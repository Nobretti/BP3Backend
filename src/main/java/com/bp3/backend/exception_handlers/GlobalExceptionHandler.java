package com.bp3.backend.exception_handlers;

import com.bp3.backend.common.Messages;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the BP3 Process Diagram Reducer application.
 * 
 * <p>This handler provides centralized error handling for all exceptions thrown
 * by the application, ensuring consistent error responses and comprehensive logging.</p>
 * 
 * The handler covers:
 *   Validation errors (Bean Validation, JSON parsing)
 *   Business logic exceptions
 *   System errors and unexpected exceptions
 *   Request format errors
 * 
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors from Bean Validation annotations.
     * 
     * @param ex The validation exception
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn(Messages.VALIDATION_ERROR_OCCURRED, ex.getMessage());
        
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            logger.debug(Messages.VALIDATION_ERROR_FIELD, fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(Messages.VALIDATION_ERROR)
                .message(Messages.REQUEST_VALIDATION_FAILED)
                .details(errors)
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles JSON parsing errors and malformed request bodies.
     * 
     * @param ex The JSON parsing exception
     * @return ResponseEntity with parsing error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParsingException(HttpMessageNotReadableException ex) {
        logger.warn(Messages.JSON_PARSING_ERROR_OCCURRED, ex.getMessage());
        
        String errorMessage = Messages.INVALID_JSON_FORMAT;
        if (ex.getCause() instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) ex.getCause();
            errorMessage = Messages.INVALID_FORMAT_FOR_FIELD + mie.getPathReference();
        } else if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            errorMessage = Messages.INVALID_FORMAT_FOR_FIELD + ife.getPathReference();
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(Messages.JSON_PARSING_ERROR)
                .message(errorMessage)
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles business logic exceptions (IllegalArgumentException).
     * 
     * @param ex The business logic exception
     * @return ResponseEntity with business error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn(Messages.BUSINESS_LOGIC_ERROR_OCCURRED, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(Messages.BUSINESS_LOGIC_ERROR)
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles runtime exceptions and unexpected errors.
     * 
     * @param ex The runtime exception
     * @return ResponseEntity with system error details
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error(Messages.RUNTIME_ERROR_OCCURRED, ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(Messages.INTERNAL_SERVER_ERROR)
                .message(Messages.UNEXPECTED_ERROR_PROCESSING_REQUEST)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles all other unexpected exceptions.
     * 
     * @param ex The exception
     * @return ResponseEntity with generic error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error(Messages.UNEXPECTED_ERROR_OCCURRED, ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(Messages.SYSTEM_ERROR)
                .message(Messages.UNEXPECTED_SYSTEM_ERROR)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Error response model for consistent error formatting.
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, Object> details;

        private ErrorResponse() {}

        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }

        // Getters and setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }

        /**
         * Builder class for ErrorResponse.
         */
        public static class ErrorResponseBuilder {
            private ErrorResponse response = new ErrorResponse();

            public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
                response.timestamp = timestamp;
                return this;
            }

            public ErrorResponseBuilder status(int status) {
                response.status = status;
                return this;
            }

            public ErrorResponseBuilder error(String error) {
                response.error = error;
                return this;
            }

            public ErrorResponseBuilder message(String message) {
                response.message = message;
                return this;
            }

            public ErrorResponseBuilder details(Map<String, Object> details) {
                response.details = details;
                return this;
            }

            public ErrorResponse build() {
                return response;
            }
        }
    }
}