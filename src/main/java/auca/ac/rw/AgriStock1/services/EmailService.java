package auca.ac.rw.AgriStock1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmailId;

    public void sendOTP(String email, String otpCode, String purpose) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmailId);
            message.setTo(email);
            message.setSubject("Your OTP Code - " + purpose);
            message.setText(buildOTPEmailBody(otpCode, purpose));

            mailSender.send(message);
            log.info("✅ OTP sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("❌ Failed to send OTP email to: {}. Error: {}", email, e.getMessage());
            // For development: log the OTP
            log.info("====================================");
            log.info("OTP for {}: {}", email, otpCode);
            log.info("Purpose: {}", purpose);
            log.info("====================================");
        }
    }

    public void sendPasswordResetEmail(String email, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmailId);
            message.setTo(email);
            message.setSubject("Password Reset Request");
            message.setText(buildPasswordResetEmailBody(otpCode));

            mailSender.send(message);
            log.info("✅ Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("❌ Failed to send password reset email: {}", e.getMessage());
            log.info("Password reset OTP for {}: {}", email, otpCode);
        }
    }

    public void sendWelcomeEmail(String email, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmailId);
            message.setTo(email);
            message.setSubject("Welcome to Farm Inventory Management System");
            message.setText(buildWelcomeEmailBody(username));

            mailSender.send(message);
            log.info("✅ Welcome email sent to: {}", email);
        } catch (Exception e) {
            log.error("❌ Failed to send welcome email: {}", e.getMessage());
        }
    }

    private String buildOTPEmailBody(String otpCode, String purpose) {
        return String.format("""
            Hello,
            
            Your OTP code for %s is: %s
            
            This code will expire in 10 minutes.
            
            If you didn't request this code, please ignore this email.
            
            Best regards,
            Farm Inventory Management Team
            """, purpose, otpCode);
    }

    private String buildPasswordResetEmailBody(String otpCode) {
        return String.format("""
            Hello,
            
            We received a request to reset your password.
            
            Your password reset code is: %s
            
            This code will expire in 10 minutes.
            
            If you didn't request a password reset, please ignore this email and your password will remain unchanged.
            
            Best regards,
            Farm Inventory Management Team
            """, otpCode);
    }

    private String buildWelcomeEmailBody(String username) {
        return String.format("""
            Hello %s,
            
            Welcome to Farm Inventory Management System!
            
            Your account has been successfully verified and is now active.
            
            You can now login and start managing your farm inventory.
            
            Best regards,
            Farm Inventory Management Team
            """, username);
    }
}