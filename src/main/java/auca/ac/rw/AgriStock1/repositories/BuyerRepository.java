package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    // Email validation
    boolean existsByEmail(String email);
    Optional<Buyer> findByEmail(String email);

    // Search by name
    List<Buyer> findByBuyerNameContainingIgnoreCase(String name);
    List<Buyer> findByBusinessNameContainingIgnoreCase(String businessName);

    // Pagination
    @Query("""
        SELECT b FROM Buyer b
        WHERE 
            LOWER(b.buyerName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(b.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(b.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(b.businessName) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<Buyer> findAll(@Param("search") String search, Pageable pageable);
}
