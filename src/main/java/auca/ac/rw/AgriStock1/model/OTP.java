package auca.ac.rw.AgriStock1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OTPPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean isUsed = false;

    @Column
    private LocalDateTime usedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // OTP expires in 10 minutes
        expiresAt = createdAt.plusMinutes(10);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isUsed && !isExpired();
    }
}