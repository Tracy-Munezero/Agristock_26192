package auca.ac.rw.AgriStock1.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "buyers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long buyerId;

    @Column(nullable = false)
    @NotBlank(message = "Buyer name is required")
    private String buyerName;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Column
    private String businessName;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    // MANY-TO-ONE with Location (Village)
    @ManyToOne
    @JoinColumn(name = "village_id", nullable = false)
    @JsonIgnoreProperties({"cell.sector.district.province.districts"})
    private Village location;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }
}