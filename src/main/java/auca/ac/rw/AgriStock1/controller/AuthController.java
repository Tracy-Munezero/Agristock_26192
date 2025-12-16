package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.Buyer;
import auca.ac.rw.AgriStock1.model.DTO.*;
import auca.ac.rw.AgriStock1.model.Farmer;
import auca.ac.rw.AgriStock1.model.User;
import auca.ac.rw.AgriStock1.services.AuthService;
import auca.ac.rw.AgriStock1.services.EmailService;
import auca.ac.rw.AgriStock1.services.FarmerService;
import auca.ac.rw.AgriStock1.services.SendEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    // ==================== REGISTRATION WITH AUTO-LOGIN ====================

    /**
     * Register a new user (Farmer or Buyer)
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Verify registration OTP and auto-login
     * POST /api/auth/verify-otp
     * Returns: Login tokens immediately after verification
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponse> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        LoginResponse response = authService.verifyOTP(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Resend OTP (for any purpose)
     * POST /api/auth/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOTP(@RequestParam String email, @RequestParam String purpose) {
        if (purpose.equalsIgnoreCase("LOGIN")) {
            authService.requestLoginWith2FA(email);
        } else if (purpose.equalsIgnoreCase("PASSWORD_RESET")) {
            authService.requestPasswordReset(email);
        } else {
            throw new RuntimeException("Invalid purpose");
        }
        return ResponseEntity.ok("OTP resent successfully");
    }

    // ==================== TWO-FACTOR AUTHENTICATION LOGIN ====================

    /**
     * Step 1: Request 2FA OTP for login
     * POST /api/auth/login/request-2fa
     */
    @PostMapping("/login/request-2fa")
    public ResponseEntity<LoginOTPResponse> request2FA(@RequestParam String email) {
        LoginOTPResponse response = authService.requestLoginWith2FA(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2: Login with email, password, and OTP (2FA)
     * POST /api/auth/login/2fa
     */
    @PostMapping("/login/2fa")
    public ResponseEntity<LoginResponse> loginWith2FA(@Valid @RequestBody LoginWith2FARequest request) {
        LoginResponse response = authService.verifyLoginWith2FA(request);
        return ResponseEntity.ok(response);
    }

    // ==================== TRADITIONAL LOGIN (Backward Compatibility) ====================

    /**
     * Traditional login with email and password (only if 2FA is disabled)
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
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

    // ==================== 2FA MANAGEMENT ====================

    /**
     * Enable/Disable two-factor authentication
     * POST /api/auth/toggle-2fa
     */
    @PostMapping("/toggle-2fa")
    public ResponseEntity<String> toggle2FA(
            @RequestParam Long userId,
            @RequestParam boolean enable
    ) {
        String response = authService.toggle2FA(userId, enable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated/users")
    public ResponseEntity<Page<User>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "") String search
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<User> users = authService.getAllUsersPaginated(pageable, search);
        return ResponseEntity.ok(users);
    }

}
