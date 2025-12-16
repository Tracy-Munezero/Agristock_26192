package auca.ac.rw.AgriStock1.services;

import auca.ac.rw.AgriStock1.model.*;
import auca.ac.rw.AgriStock1.model.DTO.GlobalSearchResponse;
import auca.ac.rw.AgriStock1.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GlobalSearchService {

    private final EntityManager entityManager;
    private final FarmerRepository farmerRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // ==================== FARMER GLOBAL SEARCH ====================

    public GlobalSearchResponse searchForFarmer(Long farmerId, String keyword, Pageable pageable) {
        GlobalSearchResponse response = new GlobalSearchResponse();

        // Search in Products (farmer's own products)
        List<Product> products = searchProducts(farmerId, keyword);
        response.setProducts(products);

        // Search in Transactions (farmer's sales)
        List<Transaction> transactions = searchFarmerTransactions(farmerId, keyword);
        response.setTransactions(transactions);

        // Search in Inventory
        // Inventory is auto-calculated, so we just return the summary

        response.setTotalResults(products.size() + transactions.size());
        return response;
    }

    // ==================== BUYER GLOBAL SEARCH ====================

    public GlobalSearchResponse searchForBuyer(Long buyerId, String keyword, Pageable pageable) {
        GlobalSearchResponse response = new GlobalSearchResponse();

        // Search in all available Products
        List<Product> products = searchAllProducts(keyword);
        response.setProducts(products);

        // Search in buyer's Transactions
        List<Transaction> transactions = searchBuyerTransactions(buyerId, keyword);
        response.setTransactions(transactions);

        // Search in Farmers (to find sellers)
        List<Farmer> farmers = searchFarmers(keyword);
        response.setFarmers(farmers);

        response.setTotalResults(products.size() + transactions.size() + farmers.size());
        return response;
    }

    // ==================== ADMIN GLOBAL SEARCH ====================

    public GlobalSearchResponse searchForAdmin(String keyword, Pageable pageable) {
        GlobalSearchResponse response = new GlobalSearchResponse();

        // Search in all tables
        List<Farmer> farmers = searchFarmers(keyword);
        response.setFarmers(farmers);

        List<Buyer> buyers = searchBuyers(keyword);
        response.setBuyers(buyers);

        List<Product> products = searchAllProducts(keyword);
        response.setProducts(products);

        List<Transaction> transactions = searchAllTransactions(keyword);
        response.setTransactions(transactions);

        List<User> users = searchUsers(keyword);
        response.setUsers(users);

        response.setTotalResults(
                farmers.size() + buyers.size() + products.size() +
                        transactions.size() + users.size()
        );

        return response;
    }

    // ==================== SEARCH HELPER METHODS ====================

    private List<Product> searchProducts(Long farmerId, String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        // Farmer's products only
        predicates.add(cb.equal(product.get("farmer").get("farmerId"), farmerId));

        // Search in multiple fields
        if (keyword != null && !keyword.isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Predicate keywordPredicate = cb.or(
                    cb.like(cb.lower(product.get("productName")), searchPattern),
                    cb.like(cb.lower(product.get("category")), searchPattern)
            );
            predicates.add(keywordPredicate);
        }

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }

    private List<Product> searchAllProducts(String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        if (keyword != null && !keyword.isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Predicate keywordPredicate = cb.or(
                    cb.like(cb.lower(product.get("productName")), searchPattern),
                    cb.like(cb.lower(product.get("category")), searchPattern)
            );
            query.where(keywordPredicate);
        }

        return entityManager.createQuery(query).getResultList();
    }

    private List<Transaction> searchFarmerTransactions(Long farmerId, String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = query.from(Transaction.class);
        Join<Transaction, Product> product = transaction.join("product");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(product.get("farmer").get("farmerId"), farmerId));

        if (keyword != null && !keyword.isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Predicate keywordPredicate = cb.or(
                    cb.like(cb.lower(product.get("productName")), searchPattern),
                    cb.like(cb.lower(cb.toString(transaction.get("status"))), searchPattern)
            );
            predicates.add(keywordPredicate);
        }

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }

    private List<Transaction> searchBuyerTransactions(Long buyerId, String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = query.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(transaction.get("buyer").get("buyerId"), buyerId));

        if (keyword != null && !keyword.isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Join<Transaction, Product> product = transaction.join("product");
            Predicate keywordPredicate = cb.or(
                    cb.like(cb.lower(product.get("productName")), searchPattern),
                    cb.like(cb.lower(cb.toString(transaction.get("status"))), searchPattern)
            );
            predicates.add(keywordPredicate);
        }

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }

    private List<Transaction> searchAllTransactions(String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = query.from(Transaction.class);

        if (keyword != null && !keyword.isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Join<Transaction, Product> product = transaction.join("product");
            Predicate keywordPredicate = cb.or(
                    cb.like(cb.lower(product.get("productName")), searchPattern),
                    cb.like(cb.lower(cb.toString(transaction.get("status"))), searchPattern)
            );
            query.where(keywordPredicate);
        }

        return entityManager.createQuery(query).getResultList();
    }

    private List<Farmer> searchFarmers(String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Farmer> query = cb.createQuery(Farmer.class);
        Root<Farmer> farmer = query.from(Farmer.class);

        if (keyword != null && !keyword.isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Predicate keywordPredicate = cb.or(
                    cb.like(cb.lower(farmer.get("firstName")), searchPattern),
                    cb.like(cb.lower(farmer.get("lastName")), searchPattern),
                    cb.like(cb.lower(farmer.get("email")), searchPattern),
                    cb.like(cb.lower(farmer.get("phone")), searchPattern)
            );
            query.where(keywordPredicate);
        }

        return entityManager.createQuery(query).getResultList();
    }

    private List<Buyer> searchBuyers(String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Buyer> query = cb.createQuery(Buyer.class);
        Root<Buyer> buyer = query.from(Buyer.class);

        if (keyword != null && !keyword.isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Predicate keywordPredicate = cb.or(
                    cb.like(cb.lower(buyer.get("buyerName")), searchPattern),
                    cb.like(cb.lower(buyer.get("email")), searchPattern),
                    cb.like(cb.lower(buyer.get("phone")), searchPattern),
                    cb.like(cb.lower(buyer.get("businessName")), searchPattern)
            );
            query.where(keywordPredicate);
        }

        return entityManager.createQuery(query).getResultList();
    }

    private List<User> searchUsers(String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);

        if (keyword != null && !keyword.isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Predicate keywordPredicate = cb.or(
                    cb.like(cb.lower(user.get("username")), searchPattern),
                    cb.like(cb.lower(user.get("email")), searchPattern),
                    cb.like(cb.lower(cb.toString(user.get("role"))), searchPattern)
            );
            query.where(keywordPredicate);
        }

        return entityManager.createQuery(query).getResultList();
    }
}