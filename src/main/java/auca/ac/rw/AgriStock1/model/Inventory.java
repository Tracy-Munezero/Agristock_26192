package auca.ac.rw.AgriStock1.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @Column(nullable = false)
    private Integer totalProducts;

    @Column(nullable = false)
    private LocalDate lastUpdated;

    // ONE-TO-ONE with Farmer
    @OneToOne
    @JoinColumn(name = "farmer_id", nullable = false, unique = true)
    private Farmer farmer;

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        lastUpdated = LocalDate.now();
    }
}
