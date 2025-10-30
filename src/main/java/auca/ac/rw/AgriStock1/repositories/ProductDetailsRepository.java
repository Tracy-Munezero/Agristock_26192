package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {
    Optional<ProductDetails> findByProductProductId(Long productId);
    List<ProductDetails> findByExpiryDateBefore(LocalDate date);
    List<ProductDetails> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
}