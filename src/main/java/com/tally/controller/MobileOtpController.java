package com.tally.controller;

import com.tally.service.MobileOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

// @RestController
// @RequestMapping("/api/mobile-otp")
// @CrossOrigin(origins = "*")
public class MobileOtpController {

    @Autowired
    private MobileOtpService mobileOtpService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        try {
            String mobile = request.get("mobile");
            boolean sent = mobileOtpService.sendMobileOtp(mobile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", sent);
            response.put("message", sent ? "OTP sent successfully" : "Failed to send OTP");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        try {
            String mobile = request.get("mobile");
            String otp = request.get("otp");
            boolean verified = mobileOtpService.verifyMobileOtp(mobile, otp);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", verified);
            response.put("message", verified ? "OTP verified successfully" : "Invalid or expired OTP");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}