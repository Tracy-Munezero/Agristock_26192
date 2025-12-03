package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
    Page<Buyer> findAll(Pageable pageable);
}
