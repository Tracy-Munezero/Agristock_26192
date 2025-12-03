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

    // For production, integrate with SendGrid, AWS SES, or similar
    // For now, we'll log the OTP (in production, actually send email)
    @Autowired
    private JavaMailSender mailSender;

    @Value("$spring.mail.username")
    private String fromEmailId;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;

    }


    public void sendOTP(String email, String otpCode, String purpose) {
        log.info("====================================");
        log.info("Sending OTP to: {}", email);
        log.info("Purpose: {}", purpose);
        log.info("OTP Code: {}", otpCode);
        log.info("OTP expires in 10 minutes");
        log.info("====================================");



        // TODO: In production, integrate with email service:

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmailId);
        message.setTo(email);
        message.setSubject("Your OTP Code - " + purpose);
        message.setText("Your OTP code is: " + otpCode + "\n\nThis code will expire in 10 minutes.");
        mailSender.send(message);

    }

    public void sendPasswordResetEmail(String email, String otpCode) {
        log.info("====================================");
        log.info("Password Reset OTP sent to: {}", email);
        log.info("OTP Code: {}", otpCode);
        log.info("Use this code to reset your password");
        log.info("====================================");
    }

    public void sendWelcomeEmail(String email, String username) {
        log.info("====================================");
        log.info("Welcome email sent to: {}", email);
        log.info("Username: {}", username);
        log.info("====================================");

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmailId);
        message.setTo(email);
        message.setSubject("Your OTP Code - ");
        message.setText("Your OTP code is: " + "\n\nThis code will expire in 10 minutes.");
        mailSender.send(message);
    }
}