package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.Inventory;
import auca.ac.rw.AgriStock1.model.Product;
import auca.ac.rw.AgriStock1.model.ProductDetails;
import auca.ac.rw.AgriStock1.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    // ==================== CREATE ====================
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PostMapping("/with-details")
    public ResponseEntity<Product> createProductWithDetails(
            @Valid @RequestBody ProductWithDetailsRequest request
    ) {
        Product createdProduct = productService.createProductWithDetails(
                request.getProduct(),
                request.getDetails()
        );
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // ==================== READ ====================
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Product>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "") String search
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Product> products = productService.getAllProductsPaginated(pageable, search);
        return ResponseEntity.ok(products);
    }

    // ==================== UPDATE ====================
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product productDetails
    ) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    // Update stock quantity
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        Product updatedProduct = productService.updateStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    // Increase stock (add harvest)
    @PatchMapping("/{id}/increase-stock")
    public ResponseEntity<Product> increaseStock(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        Product updatedProduct = productService.increaseStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    // Decrease stock (for internal use or manual adjustment)
    @PatchMapping("/{id}/decrease-stock")
    public ResponseEntity<Product> decreaseStock(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        Product updatedProduct = productService.decreaseStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== FARMER'S PRODUCTS ====================

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<Product>> getProductsByFarmer(@PathVariable Long farmerId) {
        List<Product> products = productService.getProductsByFarmerId(farmerId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/farmer/{farmerId}/paginated")
    public ResponseEntity<Page<Product>> getProductsByFarmerPaginated(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productName") String sortBy,
            @RequestParam(defaultValue = "") String search
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Product> products = productService.getProductsByFarmerPaginated(farmerId, pageable, search);
        return ResponseEntity.ok(products);
    }

    // ==================== CATEGORY ====================

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    // ==================== INVENTORY ALERTS ====================

    // Get low stock products
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold
    ) {
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    // Get low stock for specific farmer
    @GetMapping("/farmer/{farmerId}/low-stock")
    public ResponseEntity<List<Product>> getLowStockByFarmer(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "10") Integer threshold
    ) {
        List<Product> products = productService.getLowStockByFarmer(farmerId, threshold);
        return ResponseEntity.ok(products);
    }

    // Get expiring products
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<Product>> getExpiringProducts(
            @RequestParam(defaultValue = "7") Integer days
    ) {
        List<Product> products = productService.getExpiringProducts(days);
        return ResponseEntity.ok(products);
    }

    // ==================== SEARCH & FILTER ====================

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice
    ) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    // ==================== INVENTORY ====================

    @GetMapping("/inventory/farmer/{farmerId}")
    public ResponseEntity<Inventory> getFarmerInventory(@PathVariable Long farmerId) {
        Inventory inventory = productService.getFarmerInventory(farmerId);
        return ResponseEntity.ok(inventory);
    }
}

// ==================== REQUEST DTO ====================
class ProductWithDetailsRequest {
    private Product product;
    private ProductDetails details;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductDetails getDetails() {
        return details;
    }

    public void setDetails(ProductDetails details) {
        this.details = details;
    }
}