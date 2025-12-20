package auca.ac.rw.AgriStock1.services;


import auca.ac.rw.AgriStock1.model.Farmer;
import auca.ac.rw.AgriStock1.model.Inventory;
import auca.ac.rw.AgriStock1.model.Product;
import auca.ac.rw.AgriStock1.model.ProductDetails;
import auca.ac.rw.AgriStock1.repositories.FarmerRepository;
import auca.ac.rw.AgriStock1.repositories.InventoryRepository;
import auca.ac.rw.AgriStock1.repositories.ProductDetailsRepository;
import auca.ac.rw.AgriStock1.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.sql.DriverManager.println;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final FarmerRepository farmerRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final InventoryRepository inventoryRepository;

    // ==================== CREATE ====================
    public Product createProduct(Product product) {
        // Validate farmer exists
        if (product.getFarmer() != null && product.getFarmer().getFarmerId() != null) {
            Farmer farmer = farmerRepository.findById(product.getFarmer().getFarmerId())
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            product.setFarmer(farmer);
        } else {
            throw new RuntimeException("Farmer is required");
        }

        Product savedProduct = productRepository.save(product);

        // Update inventory
        updateFarmerInventory(product.getFarmer().getFarmerId());

        return savedProduct;
    }

    // Create product with details
    public Product createProductWithDetails(Product product, ProductDetails details) {
        Product savedProduct = createProduct(product);

        // Link and save product details
        details.setProduct(savedProduct);
        productDetailsRepository.save(details);

        return savedProduct;
    }

    // ==================== READ ====================
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProductsPaginated(Pageable pageable, String search) {
        return productRepository.findAll(pageable, search);
    }

    // ==================== UPDATE ====================
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        product.setProductName(productDetails.getProductName());
        product.setCategory(productDetails.getCategory());
        product.setUnitPrice(productDetails.getUnitPrice());

        // Validate quantity is not negative
        if (productDetails.getQuantityInStock() < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }
        product.setQuantityInStock(productDetails.getQuantityInStock());

        Product updatedProduct = productRepository.save(product);

        // Update inventory
        updateFarmerInventory(product.getFarmer().getFarmerId());

        return updatedProduct;
    }

    // Update stock quantity
    public Product updateStock(Long productId, Integer newQuantity) {
        Product product = getProductById(productId);

        if (newQuantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }

        product.setQuantityInStock(newQuantity);
        Product updatedProduct = productRepository.save(product);

        // Update inventory
        updateFarmerInventory(product.getFarmer().getFarmerId());

        return updatedProduct;
    }

    // Decrease stock (for sales)
    public Product decreaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);

        if (product.getQuantityInStock() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " +
                    product.getQuantityInStock() + ", Requested: " + quantity);
        }

        product.setQuantityInStock(product.getQuantityInStock() - quantity);
        Product updatedProduct = productRepository.save(product);

        // Update inventory
        updateFarmerInventory(product.getFarmer().getFarmerId());

        return updatedProduct;
    }

    // Increase stock (for new harvest)
    public Product increaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        product.setQuantityInStock(product.getQuantityInStock() + quantity);
        Product updatedProduct = productRepository.save(product);

        // Update inventory
        updateFarmerInventory(product.getFarmer().getFarmerId());

        return updatedProduct;
    }

    // ==================== DELETE ====================
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        Long farmerId = product.getFarmer().getFarmerId();

        productRepository.deleteById(id);

        // Update inventory
        updateFarmerInventory(farmerId);
    }

    // ==================== CUSTOM QUERIES ====================

    // Get products by farmer
    public List<Product> getProductsByFarmerId(Long farmerId) {
        return productRepository.findByFarmerFarmerId(farmerId);
    }

    public Page<Product> getProductsByFarmerPaginated(Long farmerId, Pageable pageable, String search) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        println("Product found: " + productRepository.findByFarmerWithOptionalSearch(farmer, search,pageable));
        return productRepository.findByFarmerWithOptionalSearch(farmer, search,pageable);
    }

    // Get products by category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // Get low stock products
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByQuantityInStockLessThan(threshold);
    }

    // Get low stock for specific farmer
    public List<Product> getLowStockByFarmer(Long farmerId, Integer threshold) {
        return productRepository.findLowStockByFarmer(farmerId, threshold);
    }

    // Search products by name
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name);
    }

    // Get products by price range
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByUnitPriceBetween(minPrice, maxPrice);
    }

    // Get expiring products
    public List<Product> getExpiringProducts(Integer daysFromNow) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysFromNow);
        return productRepository.findProductsExpiringBetween(today, futureDate);
    }

    // ==================== INVENTORY MANAGEMENT ====================

    private void updateFarmerInventory(Long farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        // Calculate total products
        List<Product> products = productRepository.findByFarmerFarmerId(farmerId);
        int totalProducts = products.stream()
                .mapToInt(Product::getQuantityInStock)
                .sum();

        // Update or create inventory
        Inventory inventory = inventoryRepository.findByFarmerFarmerId(farmerId)
                .orElse(new Inventory());

        inventory.setFarmer(farmer);
        inventory.setTotalProducts(totalProducts);
        inventory.setLastUpdated(LocalDate.now());

        inventoryRepository.save(inventory);
    }

    public Inventory getFarmerInventory(Long farmerId) {
        return inventoryRepository.findByFarmerFarmerId(farmerId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for farmer"));
    }
}
