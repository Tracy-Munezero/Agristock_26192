package auca.ac.rw.AgriStock1.repositories;


import auca.ac.rw.AgriStock1.model.Account;
import auca.ac.rw.AgriStock1.model.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    List<Account> findByAccountStatus(AccountStatus status);
    Optional<Account> findByFarmerFarmerId(Long farmerId);
}
