package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.DTO.GlobalSearchResponse;
import auca.ac.rw.AgriStock1.services.GlobalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    /**
     * Global search for FARMER
     * Searches in: Products (own), Transactions (own sales)
     * GET /api/search/farmer/{farmerId}
     */
    @GetMapping("/farmer/{farmerId}")
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<GlobalSearchResponse> searchForFarmer(
            @PathVariable Long farmerId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        GlobalSearchResponse response = globalSearchService.searchForFarmer(farmerId, keyword, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Global search for BUYER
     * Searches in: Products (all available), Transactions (own purchases), Farmers
     * GET /api/search/buyer/{buyerId}
     */
    @GetMapping("/buyer/{buyerId}")
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
    public ResponseEntity<GlobalSearchResponse> searchForBuyer(
            @PathVariable Long buyerId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        GlobalSearchResponse response = globalSearchService.searchForBuyer(buyerId, keyword, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Global search for ADMIN
     * Searches in: ALL tables (Farmers, Buyers, Products, Transactions, Users)
     * GET /api/search/admin
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalSearchResponse> searchForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        GlobalSearchResponse response = globalSearchService.searchForAdmin(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}