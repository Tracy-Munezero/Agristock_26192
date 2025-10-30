package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.Buyer;
import auca.ac.rw.AgriStock1.services.BuyerService;
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
@RequestMapping("/api/buyers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BuyerController {

    private final BuyerService buyerService;

    // ==================== CREATE ====================
    @PostMapping
    public ResponseEntity<Buyer> createBuyer(@Valid @RequestBody Buyer buyer) {
        Buyer createdBuyer = buyerService.createBuyer(buyer);
        return new ResponseEntity<>(createdBuyer, HttpStatus.CREATED);
    }

    // ==================== READ ====================
    @GetMapping("/{id}")
    public ResponseEntity<Buyer> getBuyerById(@PathVariable Long id) {
        Buyer buyer = buyerService.getBuyerById(id);
        return ResponseEntity.ok(buyer);
    }

    @GetMapping
    public ResponseEntity<List<Buyer>> getAllBuyers() {
        List<Buyer> buyers = buyerService.getAllBuyers();
        return ResponseEntity.ok(buyers);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Buyer>> getAllBuyersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "buyerId") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Buyer> buyers = buyerService.getAllBuyersPaginated(pageable);
        return ResponseEntity.ok(buyers);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Buyer> getBuyerByEmail(@PathVariable String email) {
        Buyer buyer = buyerService.getBuyerByEmail(email);
        return ResponseEntity.ok(buyer);
    }

    // ==================== UPDATE ====================
    @PutMapping("/{id}")
    public ResponseEntity<Buyer> updateBuyer(
            @PathVariable Long id,
            @Valid @RequestBody Buyer buyerDetails
    ) {
        Buyer updatedBuyer = buyerService.updateBuyer(id, buyerDetails);
        return ResponseEntity.ok(updatedBuyer);
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuyer(@PathVariable Long id) {
        buyerService.deleteBuyer(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== LOCATION-BASED QUERIES ====================

    @GetMapping("/by-province-code/{code}")
    public ResponseEntity<List<Buyer>> getBuyersByProvinceCode(@PathVariable String code) {
        List<Buyer> buyers = buyerService.getBuyersByProvinceCode(code);
        return ResponseEntity.ok(buyers);
    }

    @GetMapping("/by-province-name/{name}")
    public ResponseEntity<List<Buyer>> getBuyersByProvinceName(@PathVariable String name) {
        List<Buyer> buyers = buyerService.getBuyersByProvinceName(name);
        return ResponseEntity.ok(buyers);
    }

    @GetMapping("/by-village/{villageId}")
    public ResponseEntity<List<Buyer>> getBuyersByVillage(@PathVariable Long villageId) {
        List<Buyer> buyers = buyerService.getBuyersByVillageId(villageId);
        return ResponseEntity.ok(buyers);
    }

    // ==================== SEARCH ====================

    @GetMapping("/search")
    public ResponseEntity<List<Buyer>> searchBuyers(@RequestParam String name) {
        List<Buyer> buyers = buyerService.searchBuyersByName(name);
        return ResponseEntity.ok(buyers);
    }

    @GetMapping("/search-business")
    public ResponseEntity<List<Buyer>> searchByBusinessName(@RequestParam String businessName) {
        List<Buyer> buyers = buyerService.searchBuyersByBusinessName(businessName);
        return ResponseEntity.ok(buyers);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = buyerService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}
