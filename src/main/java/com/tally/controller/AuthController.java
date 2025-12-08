package com.tally.controller;

import com.tally.entity.User;
import com.tally.service.UserService;
import com.tally.service.interfaces.IAuthenticationService;
import com.tally.service.interfaces.IResponseBuilder;
import com.tally.service.interfaces.IUserService;
import com.tally.websocket.SessionWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller
 * Handles user registration, login, and logout
 * Uses dependency injection with interfaces following Dependency Inversion Principle
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final IUserService userService;
    private final IAuthenticationService authenticationService;
    private final IResponseBuilder responseBuilder;
    private final SessionWebSocketHandler sessionWebSocketHandler;
    
    @Autowired
    public AuthController(
            UserService userService,
            IAuthenticationService authenticationService,
            IResponseBuilder responseBuilder,
            SessionWebSocketHandler sessionWebSocketHandler) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.responseBuilder = responseBuilder;
        this.sessionWebSocketHandler = sessionWebSocketHandler;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getLicenceNo(),
                request.getPassword(),
                request.getFullName()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Optional<User> user = userService.authenticateUser(request.getUsername(), request.getPassword());
        
        if (user.isPresent()) {
            String token = authenticationService.generateToken(user.get().getUsername());
            
            // Generate unique device token for single device login
            String deviceToken = java.util.UUID.randomUUID().toString();
            userService.updateDeviceToken(user.get().getId(), deviceToken);
            
            // Invalidate old WebSocket session if exists
            sessionWebSocketHandler.invalidateUserSession(
                user.get().getId(), 
                "You have been logged in from another device"
            );
            
            // Build response using response builder (follows Single Responsibility)
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("deviceToken", deviceToken);
            responseData.put("username", user.get().getUsername());
            responseData.put("userId", user.get().getId());
            responseData.put("role", user.get().getRole());
            responseData.put("fullName", user.get().getFullName());
            responseData.put("wsUrl", "ws://localhost:8080/session");
            
            Map<String, Object> response = responseBuilder.success("Login successful", responseData);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = responseBuilder.error("Invalid username or password", 401);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String username = authenticationService.extractUsername(jwt);
            
            Optional<User> user = userService.findByUsername(username);
            if (user.isPresent()) {
                userService.clearDeviceToken(user.get().getId());
            }
            
            Map<String, Object> response = responseBuilder.success("Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = responseBuilder.error("Logout failed: " + e.getMessage(), 400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            if (authenticationService.validateToken(jwt)) {
                String username = authenticationService.extractUsername(jwt);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("username", username);
                
                Map<String, Object> response = responseBuilder.success("Token is valid", responseData);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = responseBuilder.error("Token is invalid", 401);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = responseBuilder.error("Error validating token: " + e.getMessage(), 401);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String username = authenticationService.extractUsername(jwt);
            
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", user.getId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
                userData.put("fullName", user.getFullName());
                userData.put("role", user.getRole());
                userData.put("licenceNo", user.getLicenceNo());
                userData.put("enabled", user.isEnabled());
                
                Map<String, Object> response = responseBuilder.success("User retrieved successfully", userData);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = responseBuilder.error("User not found", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = responseBuilder.error("Error getting user: " + e.getMessage(), 401);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    // Inner classes for request bodies
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    public static class RegisterRequest {
        private String username;
        private String email;
        private Long licenceNo;
        private String password;
        private String fullName;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public Long getLicenceNo() {
            return licenceNo;
        }
        
        public void setLicenceNo(Long licenceNo) {
            this.licenceNo = licenceNo;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}
