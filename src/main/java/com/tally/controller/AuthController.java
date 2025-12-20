package com.tally.controller;

import com.tally.entity.User;
import com.tally.service.UserService;
import com.tally.service.EmailService;
import com.tally.service.OtpService;
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
    private final OtpService otpService;
    private final EmailService emailService;
    
    @Autowired
    public AuthController(
            UserService userService,
            IAuthenticationService authenticationService,
            IResponseBuilder responseBuilder,
            SessionWebSocketHandler sessionWebSocketHandler,
            OtpService otpService,
            EmailService emailService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.responseBuilder = responseBuilder;
        this.sessionWebSocketHandler = sessionWebSocketHandler;
        this.otpService = otpService;
        this.emailService = emailService;
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
            
            // Send OTP for verification
            boolean otpSent = false;
            String otpErrorMessage = null;
            try {
                otpService.sendOtp(user);
                otpSent = true;
            } catch (Exception e) {
                // Log error but don't fail registration
                System.err.println("Failed to send OTP: " + e.getMessage());
                otpErrorMessage = e.getMessage();
                
                // Check if it's AWS SES verification error
                if (e.getMessage() != null && e.getMessage().contains("Email address is not verified")) {
                    otpErrorMessage = "Email could not be sent. AWS SES is in sandbox mode - recipient email must be verified in AWS Console first.";
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("licenceNo", user.getLicenceNo());
            response.put("otpSent", otpSent);
            
            if (otpSent) {
                response.put("message", "User registered successfully. Please check your email for OTP verification code (valid for 5 minutes).");
            } else {
                response.put("message", "User registered successfully, but OTP could not be sent. " + 
                    (otpErrorMessage != null ? otpErrorMessage : "Please use the resend OTP option."));
                response.put("warning", "You can request OTP resend after registration.");
            }
            
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
        Optional<User> user = Optional.empty();
        
        // Check if login is with email+licenceNo or username
        if (request.getEmail() != null && request.getLicenceNo() != null) {
            // Login with email and licence number
            user = userService.authenticateUserByEmailAndLicence(
                request.getEmail(), 
                request.getLicenceNo(), 
                request.getPassword()
            );
        } else if (request.getUsername() != null) {
            // Login with username
            user = userService.authenticateUser(request.getUsername(), request.getPassword());
        }
        
        if (user.isPresent()) {
            // Check if email is verified
            if (!user.get().isEmailVerified()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email not verified. Please verify your email with OTP before logging in.");
                response.put("email", user.get().getEmail());
                response.put("licenceNo", user.get().getLicenceNo());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
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
            responseData.put("email", user.get().getEmail());
            responseData.put("licenceNo", user.get().getLicenceNo());
            responseData.put("wsUrl", "ws://localhost:8080/session");
            
            Map<String, Object> response = responseBuilder.success("Login successful", responseData);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = responseBuilder.error("Invalid credentials", 401);
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
    
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            String otpCode = (String) request.get("otp");
            Long licenceNo = request.get("licenceNo") != null ? 
                Long.valueOf(request.get("licenceNo").toString()) : null;
            
            if (email == null || email.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (licenceNo == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Licence number is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (otpCode == null || otpCode.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "OTP code is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            boolean verified = otpService.verifyOtp(email, licenceNo, otpCode);
            
            if (verified) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Email verified successfully. You can now login.");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid or expired OTP code");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, Object>> resendOtp(@RequestBody Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            Long licenceNo = request.get("licenceNo") != null ? 
                Long.valueOf(request.get("licenceNo").toString()) : null;
            
            if (email == null || email.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (licenceNo == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Licence number is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            otpService.resendOtp(email, licenceNo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OTP has been resent to your email. Valid for 5 minutes.");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/test-email")
    public ResponseEntity<Map<String, Object>> testEmailConnection() {
        Map<String, Object> response = new HashMap<>();
        
        boolean connected = emailService.testConnection();
        
        if (connected) {
            response.put("success", true);
            response.put("message", "AWS SES connection successful!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "AWS SES connection failed. Check logs for details.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
    
    // Inner classes for request bodies
    public static class LoginRequest {
        private String username;
        private String email;
        private Long licenceNo;
        private String password;
        
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
