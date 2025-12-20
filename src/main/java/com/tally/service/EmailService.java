package com.tally.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {
    
    @Value("${aws.ses.region:us-east-1}")
    private String awsRegion;
    
    @Value("${aws.ses.access-key}")
    private String accessKey;
    
    @Value("${aws.ses.secret-key}")
    private String secretKey;
    
    @Value("${aws.ses.from-email}")
    private String fromEmail;
    
    @Value("${aws.ses.from-name:Tally Application}")
    private String fromName;
    
    private SesClient getSesClient() {
        try {
            // System.out.println("🔧 Initializing AWS SES Client...");
            // System.out.println("   Region: " + awsRegion);
            // System.out.println("   From Email: " + fromEmail);
            
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            SesClient client = SesClient.builder()
                    .region(Region.of(awsRegion))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
            
            // System.out.println("✅ AWS SES Client initialized successfully");
            return client;
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize AWS SES Client: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize AWS SES Client", e);
        }
    }
    
    public void sendVerificationEmail(String toEmail, String username, String verificationLink) {
        String subject = "Verify Your Email Address";
        String htmlBody = buildVerificationEmailHtml(username, verificationLink);
        String textBody = "Please verify your email by clicking: " + verificationLink;
        
        sendEmail(toEmail, subject, htmlBody, textBody);
    }
    
    public void sendPasswordResetEmail(String toEmail, String username, String resetLink) {
        String subject = "Reset Your Password";
        String htmlBody = buildPasswordResetEmailHtml(username, resetLink);
        String textBody = "Reset your password by clicking: " + resetLink;
        
        sendEmail(toEmail, subject, htmlBody, textBody);
    }
    
    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = "Welcome to Tally Application";
        String htmlBody = buildWelcomeEmailHtml(username);
        String textBody = "Welcome to Tally Application, " + username + "!";
        
        sendEmail(toEmail, subject, htmlBody, textBody);
    }
    
    public void sendOtpEmail(String toEmail, String username, String otp) {
        String subject = "Your OTP Verification Code";
        String htmlBody = buildOtpEmailHtml(username, otp);
        String textBody = "Your OTP verification code is: " + otp + ". Valid for 5 minutes.";
        
        sendEmail(toEmail, subject, htmlBody, textBody);
    }
    
    public boolean testConnection() {
        try {
            // System.out.println("🧪 Testing AWS SES connection...");
            SesClient client = getSesClient();
            
            // Test by getting account sending statistics
            GetAccountSendingEnabledRequest request = GetAccountSendingEnabledRequest.builder().build();
            GetAccountSendingEnabledResponse response = client.getAccountSendingEnabled(request);
            
            // System.out.println("✅ AWS SES connection successful!");
            // System.out.println("   Account sending enabled: " + response.enabled());
            client.close();
            return true;
        } catch (Exception e) {
            System.err.println("❌ AWS SES connection test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void sendEmail(String toEmail, String subject, String htmlBody, String textBody) {
        try (SesClient sesClient = getSesClient()) {
            Destination destination = Destination.builder()
                    .toAddresses(toEmail)
                    .build();
            
            Content subjectContent = Content.builder()
                    .data(subject)
                    .build();
            
            Content htmlContent = Content.builder()
                    .data(htmlBody)
                    .build();
            
            Content textContent = Content.builder()
                    .data(textBody)
                    .build();
            
            Body body = Body.builder()
                    .html(htmlContent)
                    .text(textContent)
                    .build();
            
            Message message = Message.builder()
                    .subject(subjectContent)
                    .body(body)
                    .build();
            
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(destination)
                    .message(message)
                    .build();
            
            SendEmailResponse response = sesClient.sendEmail(emailRequest);
            // System.out.println("📧 Email sent successfully! Message ID: " + response.messageId());
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private String buildVerificationEmailHtml(String username, String verificationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .button { display: inline-block; padding: 12px 30px; background: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Email Verification</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>Thank you for registering with Tally Application.</p>
                        <p>Please click the button below to verify your email address:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Verify Email Address</a>
                        </p>
                        <p>Or copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p><strong>This link will expire in 24 hours.</strong></p>
                        <p>If you didn't create an account, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Tally Application. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, verificationLink, verificationLink);
    }
    
    private String buildPasswordResetEmailHtml(String username, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #FF5722; color: white; padding: 20px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .button { display: inline-block; padding: 12px 30px; background: #FF5722; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>We received a request to reset your password.</p>
                        <p>Click the button below to reset your password:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Reset Password</a>
                        </p>
                        <p>Or copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p><strong>This link will expire in 1 hour.</strong></p>
                        <p>If you didn't request a password reset, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Tally Application. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, resetLink, resetLink);
    }
    
    private String buildWelcomeEmailHtml(String username) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome!</h1>
                    </div>
                    <div class="content">
                        <h2>Welcome %s!</h2>
                        <p>Your email has been verified successfully.</p>
                        <p>You can now access all features of the Tally Application.</p>
                        <p>If you have any questions, feel free to contact our support team.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Tally Application. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username);
    }
    
    private String buildOtpEmailHtml(String username, String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #FF9800; color: white; padding: 20px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; text-align: center; }
                    .otp-box { background: #fff; border: 2px dashed #FF9800; padding: 20px; margin: 20px 0; font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #FF9800; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>OTP Verification</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>Your OTP verification code is:</p>
                        <div class="otp-box">%s</div>
                        <p><strong>This OTP is valid for 5 minutes only.</strong></p>
                        <p>Please enter this code in the application to verify your email address.</p>
                        <p>If you didn't request this code, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Tally Application. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, otp);
    }
}
