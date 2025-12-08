package com.tally.controller;

import com.tally.entity.User;
import com.tally.service.UserService;
import com.tally.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Get current authenticated user details
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            
            if (!jwtUtil.isTokenValid(jwt)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String username = jwtUtil.extractUsername(jwt);
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("fullName", user.getFullName());
                response.put("role", user.getRole());
                response.put("licenceNo", user.getLicenceNo());
                response.put("enabled", user.isEnabled());
                response.put("createdAt", user.getCreatedAt());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error getting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * Get user by ID (admin only)
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            
            if (!jwtUtil.isTokenValid(jwt)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Optional<User> userOpt = userService.findById(id);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("fullName", user.getFullName());
                response.put("role", user.getRole());
                response.put("licenceNo", user.getLicenceNo());
                response.put("enabled", user.isEnabled());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error getting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
