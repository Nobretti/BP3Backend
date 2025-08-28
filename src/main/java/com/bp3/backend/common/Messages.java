package com.bp3.backend.common;

/**
 * Centralized message constants for the application.
 * 
 * This class contains all string literals used throughout the application
 * to ensure consistency and ease of maintenance.
 */
public final class Messages {

    private Messages() {
        // Utility class - prevent instantiation
    }

    // Exception Messages
    public static final String UNEXPECTED_ERROR = "Unexpected error";
    public static final String INVALID_STATE = "Invalid state";
    public static final String NO_HUMAN_TASKS_FOUND = "No human tasks found in the diagram";
    public static final String INVALID_DIAGRAM_STRUCTURE = "Invalid diagram structure";
    public static final String MISSING_REQUIRED_NODES = "Missing required nodes (Start/End)";
    public static final String EMPTY_DIAGRAM = "Diagram cannot be empty";
    public static final String INVALID_DIAGRAM = "Invalid diagram structure";
    public static final String START_NODE_NOT_FOUND = "Start node not found";
    public static final String END_NODE_NOT_FOUND = "End node not found";

    // Logging Messages
    public static final String PROCESSING_DIAGRAM_REDUCTION = "Processing diagram reduction request. Input: {} nodes, {} edges";
    public static final String DIAGRAM_REDUCTION_COMPLETED = "Diagram reduction completed successfully. Output: {} nodes, {} edges";
    public static final String INVALID_INPUT_PROVIDED = "Invalid input provided: {}";
    public static final String UNEXPECTED_ERROR_DURING_PROCESSING = "Unexpected error during diagram processing";
    public static final String VALIDATION_ERROR_OCCURRED = "Validation error occurred: {}";
    public static final String VALIDATION_ERROR_FIELD = "Validation error - Field: {}, Message: {}";
    public static final String JSON_PARSING_ERROR_OCCURRED = "JSON parsing error occurred: {}";
    public static final String BUSINESS_LOGIC_ERROR_OCCURRED = "Business logic error occurred: {}";
    public static final String RUNTIME_ERROR_OCCURRED = "Runtime error occurred";
    public static final String UNEXPECTED_ERROR_OCCURRED = "Unexpected error occurred";

    // Request ID Format
    public static final String REQUEST_ID_FORMAT = "req-{}-{}";

    // Error Response Messages
    public static final String VALIDATION_ERROR = "Validation Error";
    public static final String REQUEST_VALIDATION_FAILED = "Request validation failed";
    public static final String INVALID_JSON_FORMAT = "Invalid JSON format";
    public static final String INVALID_FORMAT_FOR_FIELD = "Invalid format for field: ";
    public static final String JSON_PARSING_ERROR = "JSON Parsing Error";
    public static final String BUSINESS_LOGIC_ERROR = "Business Logic Error";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String UNEXPECTED_ERROR_PROCESSING_REQUEST = "An unexpected error occurred while processing the request";
    public static final String SYSTEM_ERROR = "System Error";
    public static final String UNEXPECTED_SYSTEM_ERROR = "An unexpected system error occurred";
}
