package auca.ac.rw.AgriStock1.services;

import auca.ac.rw.AgriStock1.model.Account;
import auca.ac.rw.AgriStock1.model.AccountStatus;
import auca.ac.rw.AgriStock1.model.Farmer;
import auca.ac.rw.AgriStock1.repositories.AccountRepository;
import auca.ac.rw.AgriStock1.repositories.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final FarmerRepository farmerRepository;

    // CREATE
    public Account createAccount(Account account) {
        if (accountRepository.existsByUsername(account.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (account.getFarmer() != null && account.getFarmer().getFarmerId() != null) {
            Farmer farmer = farmerRepository.findById(account.getFarmer().getFarmerId())
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            account.setFarmer(farmer);
        }

        return accountRepository.save(account);
    }

    // READ
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found with username: " + username));
    }

    public Account getAccountByFarmerId(Long farmerId) {
        return accountRepository.findByFarmerFarmerId(farmerId)
                .orElseThrow(() -> new RuntimeException("Account not found for farmer"));
    }

    // UPDATE
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = getAccountById(id);

        if (!account.getUsername().equals(accountDetails.getUsername())) {
            if (accountRepository.existsByUsername(accountDetails.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            account.setUsername(accountDetails.getUsername());
        }

        if (accountDetails.getPassword() != null && !accountDetails.getPassword().isEmpty()) {
            account.setPassword(accountDetails.getPassword());
        }

        if (accountDetails.getAccountStatus() != null) {
            account.setAccountStatus(accountDetails.getAccountStatus());
        }

        return accountRepository.save(account);
    }

    public Account updateAccountStatus(Long id, AccountStatus status) {
        Account account = getAccountById(id);
        account.setAccountStatus(status);
        return accountRepository.save(account);
    }

    // DELETE
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    // CUSTOM QUERIES
    public List<Account> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findByAccountStatus(status);
    }

    public boolean usernameExists(String username) {
        return accountRepository.existsByUsername(username);
    }
}
