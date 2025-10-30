package auca.ac.rw.AgriStock1.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "product_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @Column(nullable = false)
    @PastOrPresent(message = "Harvest date cannot be in the future")
    private LocalDate harvestDate;

    // ONE-TO-ONE with Product
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @PrePersist
    protected void onCreate() {
        if (harvestDate == null) {
            harvestDate = LocalDate.now();
        }
    }
}
