package com.tally.service;

import com.tally.entity.User;
import com.tally.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_RESEND_ATTEMPTS = 3;
    private static final int RESEND_INTERVAL_SECONDS = 60;
    
    /**
     * Generate and send OTP to user's email
     */
    @Transactional
    public void sendOtp(User user) {
        // Generate 6-digit OTP
        String otp = generateOtp();
        
        // Set OTP expiry (5 minutes from now)
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        // Update user with OTP and reset resend count
        user.setOtpCode(otp);
        user.setOtpExpiry(expiry);
        user.setOtpSentAt(LocalDateTime.now());
        user.setOtpVerified(false);
        user.setOtpResendCount(0);
        user.setOtpLastResendAt(null);
        userRepository.save(user);
        
        // Send OTP via email
        emailService.sendOtpEmail(user.getEmail(), user.getUsername(), otp);
        
        System.out.println("📧 OTP sent to: " + user.getEmail() + " (OTP: " + otp + ")");
    }
    
    /**
     * Verify OTP entered by user with email and licenceNo
     */
    @Transactional
    public boolean verifyOtp(String email, Long licenceNo, String otpCode) {
        Optional<User> userOpt = userRepository.findByEmailAndLicenceNo(email, licenceNo);
        
        if (userOpt.isEmpty()) {
            // System.out.println("❌ User not found with email: " + email + " and licenceNo: " + licenceNo);
            return false;
        }
        
        User user = userOpt.get();
        
        // Check if OTP exists
        if (user.getOtpCode() == null || user.getOtpCode().isEmpty()) {
            // System.out.println("❌ No OTP found for user: " + user.getUsername());
            return false;
        }
        
        // Check if OTP is expired
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            // System.out.println("❌ OTP expired for user: " + user.getUsername());
            return false;
        }
        
        // Check if OTP matches
        if (!user.getOtpCode().equals(otpCode)) {
            // System.out.println("❌ Invalid OTP for user: " + user.getUsername());
            return false;
        }
        
        // Mark OTP as verified
        user.setOtpVerified(true);
        user.setEmailVerified(true); // Also mark email as verified
        user.setOtpCode(null); // Clear OTP after verification
        user.setOtpExpiry(null);
        user.setOtpResendCount(0);
        user.setOtpLastResendAt(null);
        userRepository.save(user);
        
        // Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        
        System.out.println("✅ OTP verified for user: " + user.getUsername());
        return true;
    }
    
    /**
     * Resend OTP to user with limits: max 3 times, 60 seconds interval
     */
    @Transactional
    public void resendOtp(String email, Long licenceNo) {
        Optional<User> userOpt = userRepository.findByEmailAndLicenceNo(email, licenceNo);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email + " and licence number: " + licenceNo);
        }
        
        User user = userOpt.get();
        
        if (user.isOtpVerified()) {
            throw new RuntimeException("Email already verified");
        }
        
        // Check resend count limit (max 3 attempts)
        if (user.getOtpResendCount() != null && user.getOtpResendCount() >= MAX_RESEND_ATTEMPTS) {
            throw new RuntimeException("Maximum OTP resend attempts reached. Please wait 5 minutes for OTP to expire and try again.");
        }
        
        // Check 60-second interval between resends
        if (user.getOtpLastResendAt() != null) {
            long secondsSinceLastResend = java.time.Duration.between(
                user.getOtpLastResendAt(), 
                LocalDateTime.now()
            ).getSeconds();
            
            if (secondsSinceLastResend < RESEND_INTERVAL_SECONDS) {
                long remainingSeconds = RESEND_INTERVAL_SECONDS - secondsSinceLastResend;
                throw new RuntimeException("Please wait " + remainingSeconds + " seconds before requesting another OTP");
            }
        }
        
        // Generate new OTP
        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        // Update resend tracking
        int currentCount = user.getOtpResendCount() != null ? user.getOtpResendCount() : 0;
        user.setOtpCode(otp);
        user.setOtpExpiry(expiry);
        user.setOtpSentAt(LocalDateTime.now());
        user.setOtpResendCount(currentCount + 1);
        user.setOtpLastResendAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Send OTP via email
        emailService.sendOtpEmail(user.getEmail(), user.getUsername(), otp);
        
        int remainingAttempts = MAX_RESEND_ATTEMPTS - user.getOtpResendCount();
        System.out.println("📧 OTP resent to: " + user.getEmail() + " (Attempts remaining: " + remainingAttempts + ")");
    }
    
    /**
     * Check if user's OTP is verified by email and licenceNo combination
     */
    public boolean isOtpVerified(String email, Long licenceNo) {
        Optional<User> userOpt = userRepository.findByEmailAndLicenceNo(email, licenceNo);
        return userOpt.map(User::isOtpVerified).orElse(false);
    }
    
    /**
     * Generate random 6-digit OTP
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit number
        return String.valueOf(otp);
    }
}
