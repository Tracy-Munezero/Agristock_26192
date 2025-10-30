package auca.ac.rw.AgriStock1.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // ONE-TO-ONE with Farmer (prevent circular reference)
    @OneToOne
    @JoinColumn(name = "farmer_id", nullable = false, unique = true)
    @JsonIgnore
    private Farmer farmer;

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        lastUpdated = LocalDate.now();
    }
}