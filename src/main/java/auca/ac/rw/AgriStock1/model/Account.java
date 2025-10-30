package auca.ac.rw.AgriStock1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(nullable = false)
    private LocalDate createdDate;

    // ONE-TO-ONE with Farmer
    @OneToOne
    @JoinColumn(name = "farmer_id", nullable = false, unique = true)
    private Farmer farmer;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDate.now();
        if (accountStatus == null) {
            accountStatus = AccountStatus.ACTIVE;
        }
    }
}