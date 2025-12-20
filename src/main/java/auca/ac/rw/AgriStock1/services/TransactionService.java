package auca.ac.rw.AgriStock1.services;

import auca.ac.rw.AgriStock1.model.Buyer;
import auca.ac.rw.AgriStock1.model.Product;
import auca.ac.rw.AgriStock1.model.Transaction;
import auca.ac.rw.AgriStock1.model.TransactionStatus;
import auca.ac.rw.AgriStock1.repositories.BuyerRepository;
import auca.ac.rw.AgriStock1.repositories.ProductRepository;
import auca.ac.rw.AgriStock1.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.sql.DriverManager.println;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final BuyerRepository buyerRepository;
    private final ProductService productService;

    // ==================== CREATE ====================
    public Transaction createTransaction(Transaction transaction) {
        // Validate product exists
        Product product = productRepository.findById(transaction.getProduct().getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Validate buyer exists
        Buyer buyer = buyerRepository.findById(transaction.getBuyer().getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // Check stock availability
        if (product.getQuantityInStock() < transaction.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " +
                    product.getQuantityInStock() + ", Requested: " + transaction.getQuantity());
        }

        // Calculate total amount
        Double totalAmount = product.getUnitPrice() * transaction.getQuantity();
        transaction.setTotalAmount(totalAmount);

        // Set product and buyer
        transaction.setProduct(product);
        transaction.setBuyer(buyer);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Decrease product stock
        productService.decreaseStock(product.getProductId(), transaction.getQuantity());

        return savedTransaction;
    }

    // ==================== READ ====================
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Page<Transaction> getAllTransactionsPaginated(Pageable pageable, String search, Long productId) {
        if (productId != null) {
            // Check if product exists
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Filter transactions by product
        }
        return transactionRepository.findAll(search ,pageable, productId);
    }



    // ==================== UPDATE ====================
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = getTransactionById(id);

        // Only allow updating status for existing transactions
        transaction.setStatus(transactionDetails.getStatus());

        return transactionRepository.save(transaction);
    }

    // Update transaction status
    public Transaction updateTransactionStatus(Long id, TransactionStatus status) {
        Transaction transaction = getTransactionById(id);
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }

    // Cancel transaction (restore stock)
    public Transaction cancelTransaction(Long id) {
        Transaction transaction = getTransactionById(id);

        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            throw new RuntimeException("Transaction is already cancelled");
        }

        // Restore stock
        productService.increaseStock(
                transaction.getProduct().getProductId(),
                transaction.getQuantity()
        );

        transaction.setStatus(TransactionStatus.CANCELLED);
        return transactionRepository.save(transaction);
    }

    // ==================== DELETE ====================
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // ==================== CUSTOM QUERIES ====================

    // Get transactions by buyer
    public List<Transaction> getTransactionsByBuyerId(Long buyerId) {
        return transactionRepository.findByBuyerBuyerId(buyerId);
    }

    public Page<Transaction> getTransactionsByBuyerPaginated(Long buyerId, Pageable pageable, Long productId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        println("Buyer found: " + buyer.getBuyerName());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        println("Product found: " + product.getProductName());

        return transactionRepository.findByBuyerAndOptionalProduct(buyer,product, pageable);
    }

    // Get transactions by product
    public List<Transaction> getTransactionsByProductId(Long productId) {
        return transactionRepository.findByProductProductId(productId);
    }

    // Get transactions by farmer
    public List<Transaction> getTransactionsByFarmerId(Long farmerId) {
        return transactionRepository.findByFarmerId(farmerId);
    }

//    public Page<Transaction> getTransactionsByFarmerPaginated(Long farmerId, Pageable pageable) {
//        return transactionRepository.findByFarmerId(farmerId, pageable);
//    }

    // Get transactions by date range
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate);
    }

    public Page<Transaction> getTransactionsByDateRangePaginated(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate, pageable);
    }

    // Get transactions by status
    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }

    // Get completed transactions in date range
    public List<Transaction> getCompletedTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findCompletedTransactionsBetween(startDate, endDate);
    }

    // ==================== REPORTS & ANALYTICS ====================

    // Get total sales for a farmer
    public Double getTotalSalesByFarmer(Long farmerId) {
        Double total = transactionRepository.getTotalSalesByFarmer(farmerId);
        return total != null ? total : 0.0;
    }

    // Get sales report for farmer
    public SalesReportDTO getFarmerSalesReport(Long farmerId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByFarmerId(farmerId);

        // Filter by date and status
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> !t.getTransactionDate().isBefore(start) &&
                        !t.getTransactionDate().isAfter(end) &&
                        t.getStatus() == TransactionStatus.COMPLETED)
                .toList();

        Double totalSales = filteredTransactions.stream()
                .mapToDouble(Transaction::getTotalAmount)
                .sum();

        Integer totalQuantitySold = filteredTransactions.stream()
                .mapToInt(Transaction::getQuantity)
                .sum();

        return new SalesReportDTO(
                farmerId,
                startDate,
                endDate,
                filteredTransactions.size(),
                totalQuantitySold,
                totalSales
        );
    }

    // Count transactions for a product
    public Long countTransactionsByProduct(Long productId) {
        return transactionRepository.countByProductProductId(productId);
    }
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
