package auca.ac.rw.AgriStock1.repositories;
import auca.ac.rw.AgriStock1.model.Farmer;
import auca.ac.rw.AgriStock1.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find by farmer
    List<Product> findByFarmerFarmerId(Long farmerId);
    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    Page<Product> findByFarmer(Farmer farmer, Pageable pageable, @Param("keyword") String keyword);

    // Category queries
    List<Product> findByCategory(String category);
    List<Product> findByCategoryOrderByProductNameAsc(String category);

    // Stock queries
    List<Product> findByQuantityInStockLessThan(Integer threshold);
    List<Product> findByQuantityInStockGreaterThan(Integer threshold);
    List<Product> findByQuantityInStock(Integer quantity);

    // Search by name
    List<Product> findByProductNameContainingIgnoreCase(String name);

    // Price range
    List<Product> findByUnitPriceBetween(Double minPrice, Double maxPrice);

    // Expiring products (using join with ProductDetails)
    @Query("SELECT p FROM Product p JOIN p.productDetails pd " +
            "WHERE pd.expiryDate BETWEEN :startDate AND :endDate")
    List<Product> findProductsExpiringBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Low stock by farmer
    @Query("SELECT p FROM Product p WHERE p.farmer.farmerId = :farmerId " +
            "AND p.quantityInStock < :threshold")
    List<Product> findLowStockByFarmer(
            @Param("farmerId") Long farmerId,
            @Param("threshold") Integer threshold
    );

    @Query("""
    SELECT p FROM Product p
    WHERE 
        LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(p.category) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<Product> findAll(Pageable pageable, @Param("search") String search);
}
