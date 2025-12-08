package com.tally.service.impl;

import com.tally.service.interfaces.IAuthenticationService;
import com.tally.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Authentication Service Implementation
 * Handles JWT token generation and validation
 * Follows Single Responsibility Principle (SRP) and Dependency Inversion Principle (DIP)
 */
@Service
public class AuthenticationService implements IAuthenticationService {
    
    private final JwtUtil jwtUtil;
    
    @Autowired
    public AuthenticationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }
    
    @Override
    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtUtil.isTokenValid(token) && !isTokenExpired(token);
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        return jwtUtil.isTokenExpired(token);
    }
}
