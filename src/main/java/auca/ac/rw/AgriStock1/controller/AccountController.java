package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.Account;
import auca.ac.rw.AgriStock1.model.AccountStatus;
import auca.ac.rw.AgriStock1.model.DTO.AccountRequest;
import auca.ac.rw.AgriStock1.model.Farmer;
import auca.ac.rw.AgriStock1.services.AccountService;
import auca.ac.rw.AgriStock1.services.FarmerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;



    // CREATE
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountRequest req) {
        Farmer farmer = accountService.getFarmerIdForAccount(req.getFarmerId());
        Account account = new Account();
        account.setUsername(req.getUsername());
        account.setPassword(req.getPassword());
        account.setAccountStatus(req.getAccountStatus());
        account.setFarmer(farmer);
        Account createdAccount = accountService.createAccount(account);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    // READ
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Account> getAccountByUsername(@PathVariable String username) {
        Account account = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<Account> getAccountByFarmerId(@PathVariable Long farmerId) {
        Account account = accountService.getAccountByFarmerId(farmerId);
        return ResponseEntity.ok(account);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody Account accountDetails
    ) {
        Account updatedAccount = accountService.updateAccount(id, accountDetails);
        return ResponseEntity.ok(updatedAccount);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Account> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status
    ) {
        Account updatedAccount = accountService.updateAccountStatus(id, status);
        return ResponseEntity.ok(updatedAccount);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    // CUSTOM QUERIES
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Account>> getAccountsByStatus(@PathVariable AccountStatus status) {
        List<Account> accounts = accountService.getAccountsByStatus(status);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean exists = accountService.usernameExists(username);
        return ResponseEntity.ok(exists);
    }
}