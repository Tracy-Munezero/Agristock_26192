package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.RefreshToken;
import auca.ac.rw.AgriStock1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUser(User user);

    void deleteByUser(User user);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
