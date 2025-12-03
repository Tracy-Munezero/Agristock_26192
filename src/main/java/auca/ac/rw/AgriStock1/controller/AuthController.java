package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.DTO.*;
import auca.ac.rw.AgriStock1.services.AuthService;
import auca.ac.rw.AgriStock1.services.EmailService;
import auca.ac.rw.AgriStock1.services.SendEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SendEmailService sendEmailService;
    // ==================== REGISTRATION ====================

    /**
     * Register a new user (Farmer or Buyer)
     * POST /api/auth/register
     */
    @GetMapping("/register")
    public String register() {
//        RegisterResponse response = authService.register(request);
//        emailService.sendWelcomeEmail("ygahamanyi26@gmail.com", "Yvette");
        sendEmailService.sendEmail("ygahamanyi26@gmail.com", "body", "sbj");
        return "Success";
    }

    /**
     * Verify registration OTP
     * POST /api/auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOTPResponse> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        VerifyOTPResponse response = authService.verifyOTP(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Resend OTP (for any purpose)
     * POST /api/auth/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOTP(@RequestParam String email, @RequestParam String purpose) {
        if (purpose.equalsIgnoreCase("LOGIN")) {
            authService.requestLoginOTP(email);
        } else if (purpose.equalsIgnoreCase("PASSWORD_RESET")) {
            authService.requestPasswordReset(email);
        } else {
            throw new RuntimeException("Invalid purpose");
        }
        return ResponseEntity.ok("OTP resent successfully");
    }

    // ==================== LOGIN ====================

    /**
     * Traditional login with email and password
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Request login OTP (passwordless login)
     * POST /api/auth/login/request-otp
     */
    @PostMapping("/login/request-otp")
    public ResponseEntity<LoginOTPResponse> requestLoginOTP(@RequestParam String email) {
        LoginOTPResponse response = authService.requestLoginOTP(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify login OTP and get access token
     * POST /api/auth/login/verify-otp
     */
    @PostMapping("/login/verify-otp")
    public ResponseEntity<LoginResponse> verifyLoginOTP(@Valid @RequestBody LoginOTPVerifyRequest request) {
        LoginResponse response = authService.verifyLoginOTP(request);
        return ResponseEntity.ok(response);
    }

    // ==================== PASSWORD RESET ====================

    /**
     * Request password reset OTP
     * POST /api/auth/password/reset-request
     */
    @PostMapping("/password/reset-request")
    public ResponseEntity<PasswordResetOTPResponse> requestPasswordReset(@RequestParam String email) {
        PasswordResetOTPResponse response = authService.requestPasswordReset(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Reset password using OTP
     * POST /api/auth/password/reset
     */
    @PostMapping("/password/reset")
    public ResponseEntity<PasswordResetResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        PasswordResetResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    // ==================== TOKEN MANAGEMENT ====================

    /**
     * Refresh access token using refresh token
     * POST /api/auth/refresh-token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestParam String refreshToken) {
        RefreshTokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout - invalidate refresh token
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

}
