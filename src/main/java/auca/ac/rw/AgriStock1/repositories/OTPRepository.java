package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.OTP;
import auca.ac.rw.AgriStock1.model.OTPPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByEmailAndOtpCodeAndPurpose(String email, String otpCode, OTPPurpose purpose);

    List<OTP> findByEmailAndPurpose(String email, OTPPurpose purpose);

    List<OTP> findByEmail(String email);

    // Delete expired OTPs
    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    // Delete used OTPs
    void deleteByIsUsedTrue();
}

