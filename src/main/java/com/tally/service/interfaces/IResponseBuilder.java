package com.tally.service.interfaces;

import java.util.Map;

/**
 * Interface for Response Builder Service
 * Handles standardized API response formatting
 * Follows Single Responsibility Principle (SRP)
 */
public interface IResponseBuilder {
    
    /**
     * Build success response
     */
    Map<String, Object> success(String message, Object data);
    
    /**
     * Build success response without data
     */
    Map<String, Object> success(String message);
    
    /**
     * Build error response
     */
    Map<String, Object> error(String message);
    
    /**
     * Build error response with status code
     */
    Map<String, Object> error(String message, int statusCode);
    
    /**
     * Build paginated response
     */
    Map<String, Object> paginated(String message, Object data, int page, int pageSize, long total);
}
