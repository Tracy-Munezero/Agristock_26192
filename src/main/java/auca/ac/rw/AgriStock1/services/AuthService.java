package auca.ac.rw.AgriStock1.services;

import auca.ac.rw.AgriStock1.model.*;
import auca.ac.rw.AgriStock1.model.DTO.*;
import auca.ac.rw.AgriStock1.repositories.*;
import auca.ac.rw.AgriStock1.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FarmerRepository farmerRepository;
    private final BuyerRepository buyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    private final EmailService emailService;

    // ==================== REGISTRATION ====================

    public RegisterResponse register(RegisterRequest request) {
        // Validate email and username uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Validate role
        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Must be FARMER or BUYER");
        }

        if (role == UserRole.ADMIN) {
            throw new RuntimeException("Cannot register as ADMIN through this endpoint");
        }

        // Create user account
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setIsVerified(false);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // Create corresponding Farmer or Buyer entity
        if (role == UserRole.FARMER) {
            Farmer farmer = new Farmer();
            farmer.setFirstName(request.getFirstName());
            farmer.setLastName(request.getLastName());
            farmer.setEmail(request.getEmail());
            farmer.setPhone(request.getPhone());
            farmer.setRegistrationDate(java.time.LocalDate.now());


            Farmer savedFarmer = farmerRepository.save(farmer);
            savedUser.setFarmerId(savedFarmer.getFarmerId());
        } else if (role == UserRole.BUYER) {
            Buyer buyer = new Buyer();
            buyer.setBuyerName(request.getFirstName() + " " + request.getLastName());
            buyer.setEmail(request.getEmail());
            buyer.setPhone(request.getPhone());
            buyer.setBusinessName(request.getBusinessName());
            buyer.setRegistrationDate(LocalDateTime.now());


            Buyer savedBuyer = buyerRepository.save(buyer);
            savedUser.setBuyerId(savedBuyer.getBuyerId());
        }

        userRepository.save(savedUser);

        // Generate and send OTP
        String otpCode = generateOTP();
        saveOTP(request.getEmail(), otpCode, OTPPurpose.REGISTRATION);
        emailService.sendOTP(request.getEmail(), otpCode, "REGISTRATION");

        return new RegisterResponse(
                "Registration successful! OTP sent to " + request.getEmail(),
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getRole().toString()
        );
    }

    // ==================== VERIFY OTP ====================

    public VerifyOTPResponse verifyOTP(VerifyOTPRequest request) {
        // Find OTP
        OTP otp = otpRepository.findByEmailAndOtpCodeAndPurpose(
                request.getEmail(),
                request.getOtpCode(),
                OTPPurpose.valueOf(request.getPurpose().toUpperCase())
        ).orElseThrow(() -> new RuntimeException("Invalid OTP"));

        // Check if OTP is valid
        if (!otp.isValid()) {
            throw new RuntimeException("OTP has expired or already been used");
        }

        // Mark OTP as used
        otp.setIsUsed(true);
        otp.setUsedAt(LocalDateTime.now());
        otpRepository.save(otp);

        // If registration OTP, verify user
        if (otp.getPurpose() == OTPPurpose.REGISTRATION) {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setIsVerified(true);
            userRepository.save(user);

            emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        }

        return new VerifyOTPResponse("OTP verified successfully", true);
    }

    // ==================== LOGIN WITH OTP ====================

    public LoginOTPResponse requestLoginOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        if (!user.getIsVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        // Generate and send OTP
        String otpCode = generateOTP();
        saveOTP(email, otpCode, OTPPurpose.LOGIN);
        emailService.sendOTP(email, otpCode, "LOGIN");

        return new LoginOTPResponse("OTP sent to " + email);
    }

    public LoginResponse verifyLoginOTP(LoginOTPVerifyRequest request) {
        // Verify OTP
        OTP otp = otpRepository.findByEmailAndOtpCodeAndPurpose(
                request.getEmail(),
                request.getOtpCode(),
                OTPPurpose.LOGIN
        ).orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (!otp.isValid()) {
            throw new RuntimeException("OTP has expired or already been used");
        }

        // Mark OTP as used
        otp.setIsUsed(true);
        otp.setUsedAt(LocalDateTime.now());
        otpRepository.save(otp);

        // Get user and generate tokens
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return generateLoginResponse(user);
    }

    // ==================== TRADITIONAL LOGIN ====================

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        if (!user.getIsVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return generateLoginResponse(user);
    }

    // ==================== PASSWORD RESET ====================

    public PasswordResetOTPResponse requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate and send OTP
        String otpCode = generateOTP();
        saveOTP(email, otpCode, OTPPurpose.PASSWORD_RESET);
        emailService.sendPasswordResetEmail(email, otpCode);

        return new PasswordResetOTPResponse("Password reset OTP sent to " + email);
    }

    public PasswordResetResponse resetPassword(PasswordResetRequest request) {
        // Verify OTP
        OTP otp = otpRepository.findByEmailAndOtpCodeAndPurpose(
                request.getEmail(),
                request.getOtpCode(),
                OTPPurpose.PASSWORD_RESET
        ).orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (!otp.isValid()) {
            throw new RuntimeException("OTP has expired or already been used");
        }

        // Mark OTP as used
        otp.setIsUsed(true);
        otp.setUsedAt(LocalDateTime.now());
        otpRepository.save(otp);

        // Update password
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalidate all existing refresh tokens
        refreshTokenRepository.deleteByUser(user);

        return new PasswordResetResponse("Password reset successfully");
    }

    // ==================== REFRESH TOKEN ====================

    public RefreshTokenResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }

        User user = token.getUser();
        String newAccessToken = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().toString(),
                user.getUserId()
        );

        return new RefreshTokenResponse(newAccessToken, refreshToken);
    }

    // ==================== HELPER METHODS ====================

    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    private void saveOTP(String email, String otpCode, OTPPurpose purpose) {
        OTP otp = new OTP();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setPurpose(purpose);
        otpRepository.save(otp);
    }

    private LoginResponse generateLoginResponse(User user) {
        String accessToken = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().toString(),
                user.getUserId()
        );

        // Generate refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        return new LoginResponse(
                "Login successful",
                accessToken,
                savedRefreshToken.getToken(),
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getFarmerId(),
                user.getBuyerId()
        );
    }

    // ==================== LOGOUT ====================

    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}
