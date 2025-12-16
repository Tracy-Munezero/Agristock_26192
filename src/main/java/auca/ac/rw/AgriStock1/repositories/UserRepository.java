package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Buyer;
import auca.ac.rw.AgriStock1.model.User;
import auca.ac.rw.AgriStock1.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// ==================== USER REPOSITORY ====================
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByFarmerId(Long farmerId);
    Optional<User> findByBuyerId(Long buyerId);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    List<User> findByRole(UserRole role);
    List<User> findByIsVerified(Boolean isVerified);
    List<User> findByIsActive(Boolean isActive);

    // Pagination
    @Query("""
        SELECT u FROM User u
        WHERE 
            LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(u.role) LIKE LOWER(CONCAT('%', :search, '%')) OR
            CAST(u.farmerId AS string) LIKE CONCAT('%', :search, '%') OR
            CAST(u.buyerId AS string) LIKE CONCAT('%', :search, '%')
    """)
    Page<User> findAll(@Param("search") String search, Pageable pageable);
}