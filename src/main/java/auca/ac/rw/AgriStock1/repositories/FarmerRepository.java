package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Farmer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    // Email validation
    boolean existsByEmail(String email);
    Optional<Farmer> findByEmail(String email);

    // Pagination
    Page<Farmer> findAll(Pageable pageable);

    // Date range queries
    List<Farmer> findByRegistrationDateBetween(LocalDate startDate, LocalDate endDate);
    // Search by name
    List<Farmer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName
    );

    @Query("""
        SELECT f FROM Farmer f
        WHERE LOWER(f.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(f.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(f.email) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(f.phone) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<Farmer> searchFarmers(@Param("search") String search, Pageable pageable);
}
