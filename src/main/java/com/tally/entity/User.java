package com.tally.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_email_licence", columnNames = {"email", "licence_no"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "licence_no", nullable = false)
    private Long licenceNo;
    
    @Column(nullable = false)
    private String password;
    
    private String fullName;
    
    private String role;
    
    @Column(columnDefinition = "CHAR(1) default 'Y'", length = 1)
    private String enabled = "Y";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "device_token", length = 500)
    private String deviceToken;
    
    @Column(name = "device_login_at")
    private LocalDateTime deviceLoginAt;
    
    // Email Verification Fields
    @Column(name = "email_verified", columnDefinition = "CHAR(1) default 'N'", length = 1)
    private String emailVerified = "N";
    
    // OTP Verification Fields
    @Column(name = "otp_code", length = 6)
    private String otpCode;
    
    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;
    
    @Column(name = "otp_sent_at")
    private LocalDateTime otpSentAt;
    
    @Column(name = "otp_verified", columnDefinition = "CHAR(1) default 'N'", length = 1)
    private String otpVerified = "N";
    
    @Column(name = "otp_resend_count")
    private Integer otpResendCount = 0;
    
    @Column(name = "otp_last_resend_at")
    private LocalDateTime otpLastResendAt;
    
    // Helper methods for Y/N fields
    public boolean isEnabled() {
        return "Y".equalsIgnoreCase(enabled);
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled ? "Y" : "N";
    }
    
    public boolean isEmailVerified() {
        return "Y".equalsIgnoreCase(emailVerified);
    }
    
    public void setEmailVerified(boolean verified) {
        this.emailVerified = verified ? "Y" : "N";
    }
    
    public boolean isOtpVerified() {
        return "Y".equalsIgnoreCase(otpVerified);
    }
    
    public void setOtpVerified(boolean verified) {
        this.otpVerified = verified ? "Y" : "N";
    }
}
