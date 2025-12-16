package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.Transaction;
import auca.ac.rw.AgriStock1.model.TransactionStatus;
import auca.ac.rw.AgriStock1.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    // ==================== CREATE ====================
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    // ==================== READ ====================
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Transaction>> getAllTransactionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "") String search
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Transaction> transactions = transactionService.getAllTransactionsPaginated(pageable, search);
        return ResponseEntity.ok(transactions);
    }

    // ==================== UPDATE ====================
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody Transaction transactionDetails
    ) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(
            @PathVariable Long id,
            @RequestParam TransactionStatus status
    ) {
        Transaction updatedTransaction = transactionService.updateTransactionStatus(id, status);
        return ResponseEntity.ok(updatedTransaction);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Transaction> cancelTransaction(@PathVariable Long id) {
        Transaction cancelledTransaction = transactionService.cancelTransaction(id);
        return ResponseEntity.ok(cancelledTransaction);
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== BUYER TRANSACTIONS ====================

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<Transaction>> getTransactionsByBuyer(@PathVariable Long buyerId) {
        List<Transaction> transactions = transactionService.getTransactionsByBuyerId(buyerId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/buyer/{buyerId}/paginated")
    public ResponseEntity<Page<Transaction>> getTransactionsByBuyerPaginated(
            @PathVariable Long buyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"));
        Page<Transaction> transactions = transactionService.getTransactionsByBuyerPaginated(buyerId, pageable);
        return ResponseEntity.ok(transactions);
    }

    // ==================== PRODUCT TRANSACTIONS ====================

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Transaction>> getTransactionsByProduct(@PathVariable Long productId) {
        List<Transaction> transactions = transactionService.getTransactionsByProductId(productId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> countTransactionsByProduct(@PathVariable Long productId) {
        Long count = transactionService.countTransactionsByProduct(productId);
        return ResponseEntity.ok(count);
    }

    // ==================== FARMER TRANSACTIONS ====================

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<Transaction>> getTransactionsByFarmer(@PathVariable Long farmerId) {
        List<Transaction> transactions = transactionService.getTransactionsByFarmerId(farmerId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/farmer/{farmerId}/paginated")
    public ResponseEntity<Page<Transaction>> getTransactionsByFarmerPaginated(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Transaction> transactions = transactionService.getTransactionsByFarmerPaginated(farmerId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/farmer/{farmerId}/total-sales")
    public ResponseEntity<Double> getTotalSalesByFarmer(@PathVariable Long farmerId) {
        Double totalSales = transactionService.getTotalSalesByFarmer(farmerId);
        return ResponseEntity.ok(totalSales);
    }

    // ==================== DATE RANGE QUERIES ====================

    @GetMapping("/date-range")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/date-range/paginated")
    public ResponseEntity<Page<Transaction>> getTransactionsByDateRangePaginated(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"));
        Page<Transaction> transactions = transactionService.getTransactionsByDateRangePaginated(
                startDate, endDate, pageable
        );
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Transaction>> getCompletedTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<Transaction> transactions = transactionService.getCompletedTransactions(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    // ==================== STATUS QUERIES ====================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable TransactionStatus status) {
        List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
        return ResponseEntity.ok(transactions);
    }

    // ==================== REPORTS ====================

//    @GetMapping("/report/farmer/{farmerId}")
//    public ResponseEntity<SalesReportDTO> getFarmerSalesReport(
//            @PathVariable Long farmerId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        SalesReportDTO report = transactionService.getFarmerSalesReport(farmerId, startDate, endDate);
//        return ResponseEntity.ok(report);
//    }
}

// ==================== DTO CLASS ====================
class SalesReportDTO {
    private Long farmerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalTransactions;
    private Integer totalQuantitySold;
    private Double totalSales;

    public SalesReportDTO(Long farmerId, LocalDate startDate, LocalDate endDate,
                          Integer totalTransactions, Integer totalQuantitySold, Double totalSales) {
        this.farmerId = farmerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalTransactions = totalTransactions;
        this.totalQuantitySold = totalQuantitySold;
        this.totalSales = totalSales;
    }

    // Getters and Setters
    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }

    public Integer getTotalQuantitySold() { return totalQuantitySold; }
    public void setTotalQuantitySold(Integer totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; }

    public Double getTotalSales() { return totalSales; }
    public void setTotalSales(Double totalSales) { this.totalSales = totalSales; }
}