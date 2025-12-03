package auca.ac.rw.AgriStock1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "farmers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long farmerId;

    @Column(nullable = false)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Column(nullable = false)
    private LocalDate registrationDate;

    // ONE-TO-ONE with Account (exclude farmer from Account JSON)
    @OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("farmer")
    private Account account;

    // ONE-TO-MANY with Product (exclude farmer from Product JSON)
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("farmer")
    private List<Product> products;


    // ONE-TO-ONE with Inventory (exclude farmer from Inventory JSON)
    @OneToOne(mappedBy = "farmer", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("farmer")
    private Inventory inventory;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDate.now();
    }
}