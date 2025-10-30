package auca.ac.rw.AgriStock1.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    @NotBlank(message = "Product name is required")
    private String productName;

    @Column(nullable = false)
    @NotBlank(message = "Category is required")
    private String category;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double unitPrice;

    @Column(nullable = false)
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantityInStock;

    // MANY-TO-ONE with Farmer (prevent circular reference)
    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    @JsonIgnore
    private Farmer farmer;

    // ONE-TO-ONE with ProductDetails (exclude product from details)
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("product")
    private ProductDetails productDetails;

    // ONE-TO-MANY with Transaction (exclude product details)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"product", "buyer"})
    private List<Transaction> transactions;
}