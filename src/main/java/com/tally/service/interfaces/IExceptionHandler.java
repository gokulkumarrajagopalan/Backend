package com.tally.service.interfaces;

/**
 * Interface for Exception Handler Service
 * Follows Single Responsibility Principle (SRP)
 */
public interface IExceptionHandler {
    
    /**
     * Handle application exceptions
     */
    void handleException(Exception exception) throws Exception;
    
    /**
     * Log exception
     */
    void logException(Exception exception);
    
    /**
     * Get user-friendly error message
     */
    String getErrorMessage(Exception exception);
}
