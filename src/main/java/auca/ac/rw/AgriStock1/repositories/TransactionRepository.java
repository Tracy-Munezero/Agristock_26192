package auca.ac.rw.AgriStock1.repositories;
import auca.ac.rw.AgriStock1.model.Buyer;
import auca.ac.rw.AgriStock1.model.Product;
import auca.ac.rw.AgriStock1.model.Transaction;
import auca.ac.rw.AgriStock1.model.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find by buyer
    List<Transaction> findByBuyerBuyerId(Long buyerId);
    Page<Transaction> findByBuyer(Buyer buyer, Pageable pageable);

    // Find by product
    List<Transaction> findByProductProductId(Long productId);
    Page<Transaction> findByProduct(Product product, Pageable pageable);

    // Find by farmer (through product)
    @Query("SELECT t FROM Transaction t WHERE t.product.farmer.farmerId = :farmerId")
    List<Transaction> findByFarmerId(@Param("farmerId") Long farmerId);

    @Query("SELECT t FROM Transaction t WHERE t.product.farmer.farmerId = :farmerId")
    Page<Transaction> findByFarmerId(@Param("farmerId") Long farmerId, Pageable pageable);

    // Date range queries
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<Transaction> findByTransactionDateBetween(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    );

    // Status queries
    List<Transaction> findByStatus(TransactionStatus status);

    // Sales reports
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate AND t.status = 'COMPLETED'")
    List<Transaction> findCompletedTransactionsBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Total sales for a farmer
    @Query("SELECT SUM(t.totalAmount) FROM Transaction t " +
            "WHERE t.product.farmer.farmerId = :farmerId AND t.status = 'COMPLETED'")
    Double getTotalSalesByFarmer(@Param("farmerId") Long farmerId);

    // Count transactions by product
    Long countByProductProductId(Long productId);
}
