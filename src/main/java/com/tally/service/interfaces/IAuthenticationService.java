package com.tally.service.interfaces;

import java.util.Optional;

/**
 * Interface for Authentication Service
 * Handles JWT token generation and validation
 * Follows Single Responsibility Principle (SRP)
 */
public interface IAuthenticationService {
    
    /**
     * Generate JWT token for user
     */
    String generateToken(String username);
    
    /**
     * Extract username from JWT token
     */
    String extractUsername(String token);
    
    /**
     * Validate JWT token
     */
    boolean validateToken(String token);
    
    /**
     * Check if token is expired
     */
    boolean isTokenExpired(String token);
}
