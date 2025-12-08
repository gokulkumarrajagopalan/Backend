package com.tally.service.interfaces;

import com.tally.entity.User;
import java.util.Optional;

/**
 * Interface for User Service
 * Follows Interface Segregation Principle (ISP) and Dependency Inversion Principle (DIP)
 */
public interface IUserService {
    
    /**
     * Create a new user
     */
    User createUser(String username, String email, Long licenceNo, String password, String fullName);
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by ID
     */
    Optional<User> findById(Long id);
    
    /**
     * Authenticate user with username and password
     */
    Optional<User> authenticateUser(String username, String password);
    
    /**
     * Validate password
     */
    boolean validatePassword(String rawPassword, String encodedPassword);
    
    /**
     * Update device token for single-device login
     */
    void updateDeviceToken(Long userId, String deviceToken);
    
    /**
     * Get device token
     */
    String getDeviceToken(Long userId);
    
    /**
     * Clear device token on logout
     */
    void clearDeviceToken(Long userId);
}
