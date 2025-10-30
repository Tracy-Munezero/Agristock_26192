package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.Farmer;
import auca.ac.rw.AgriStock1.services.FarmerService;
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
import java.util.List;

@RestController
@RequestMapping("/api/farmers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FarmerController {

    private final FarmerService farmerService;

    // ==================== CREATE ====================
    @PostMapping
    public ResponseEntity<Farmer> createFarmer(@Valid @RequestBody Farmer farmer) {
        Farmer createdFarmer = farmerService.createFarmer(farmer);
        return new ResponseEntity<>(createdFarmer, HttpStatus.CREATED);
    }

    // ==================== READ ====================
    @GetMapping("/{id}")
    public ResponseEntity<Farmer> getFarmerById(@PathVariable Long id) {
        Farmer farmer = farmerService.getFarmerById(id);
        return ResponseEntity.ok(farmer);
    }

    @GetMapping
    public ResponseEntity<List<Farmer>> getAllFarmers() {
        List<Farmer> farmers = farmerService.getAllFarmers();
        return ResponseEntity.ok(farmers);
    }

    // GET with Pagination and Sorting
    @GetMapping("/paginated")
    public ResponseEntity<Page<Farmer>> getAllFarmersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "farmerId") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Farmer> farmers = farmerService.getAllFarmersPaginated(pageable);
        return ResponseEntity.ok(farmers);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Farmer> getFarmerByEmail(@PathVariable String email) {
        Farmer farmer = farmerService.getFarmerByEmail(email);
        return ResponseEntity.ok(farmer);
    }

    // ==================== UPDATE ====================
    @PutMapping("/{id}")
    public ResponseEntity<Farmer> updateFarmer(
            @PathVariable Long id,
            @Valid @RequestBody Farmer farmerDetails
    ) {
        Farmer updatedFarmer = farmerService.updateFarmer(id, farmerDetails);
        return ResponseEntity.ok(updatedFarmer);
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarmer(@PathVariable Long id) {
        farmerService.deleteFarmer(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== LOCATION-BASED QUERIES ====================

    // Get farmers by province code (REQUIRED ENDPOINT)
    @GetMapping("/by-province-code/{code}")
    public ResponseEntity<List<Farmer>> getFarmersByProvinceCode(@PathVariable String code) {
        List<Farmer> farmers = farmerService.getFarmersByProvinceCode(code);
        return ResponseEntity.ok(farmers);
    }

    // Get farmers by province name (REQUIRED ENDPOINT)
    @GetMapping("/by-province-name/{name}")
    public ResponseEntity<List<Farmer>> getFarmersByProvinceName(@PathVariable String name) {
        List<Farmer> farmers = farmerService.getFarmersByProvinceName(name);
        return ResponseEntity.ok(farmers);
    }

    // Get farmers by district
    @GetMapping("/by-district/{districtCode}")
    public ResponseEntity<List<Farmer>> getFarmersByDistrict(@PathVariable String districtCode) {
        List<Farmer> farmers = farmerService.getFarmersByDistrictCode(districtCode);
        return ResponseEntity.ok(farmers);
    }

    // Get farmers by village
    @GetMapping("/by-village/{villageId}")
    public ResponseEntity<List<Farmer>> getFarmersByVillage(@PathVariable Long villageId) {
        List<Farmer> farmers = farmerService.getFarmersByVillageId(villageId);
        return ResponseEntity.ok(farmers);
    }

    // Get farmer's location hierarchy
    @GetMapping("/{farmerId}/location")
    public ResponseEntity<LocationHierarchyDTO> getFarmerLocation(@PathVariable Long farmerId) {
        Farmer farmer = farmerService.getFarmerById(farmerId);
        LocationHierarchyDTO location = buildLocationHierarchy(farmer);
        return ResponseEntity.ok(location);
    }

    // ==================== SEARCH & FILTER ====================

    // Search farmers by name
    @GetMapping("/search")
    public ResponseEntity<List<Farmer>> searchFarmers(@RequestParam String name) {
        List<Farmer> farmers = farmerService.searchFarmersByName(name);
        return ResponseEntity.ok(farmers);
    }

    // Get farmers by registration date range
    @GetMapping("/by-date-range")
    public ResponseEntity<List<Farmer>> getFarmersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<Farmer> farmers = farmerService.getFarmersByRegistrationDateRange(startDate, endDate);
        return ResponseEntity.ok(farmers);
    }

    // Check if email exists
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = farmerService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    // ==================== HELPER METHODS ====================
    private LocationHierarchyDTO buildLocationHierarchy(Farmer farmer) {
        LocationHierarchyDTO dto = new LocationHierarchyDTO();
        if (farmer.getLocation() != null) {
            dto.setVillage(farmer.getLocation().getVillageName());
            dto.setVillageCode(farmer.getLocation().getVillageCode());

            if (farmer.getLocation().getCell() != null) {
                dto.setCell(farmer.getLocation().getCell().getCellName());
                dto.setCellCode(farmer.getLocation().getCell().getCellCode());

                if (farmer.getLocation().getCell().getSector() != null) {
                    dto.setSector(farmer.getLocation().getCell().getSector().getSectorName());
                    dto.setSectorCode(farmer.getLocation().getCell().getSector().getSectorCode());

                    if (farmer.getLocation().getCell().getSector().getDistrict() != null) {
                        dto.setDistrict(farmer.getLocation().getCell().getSector().getDistrict().getDistrictName());
                        dto.setDistrictCode(farmer.getLocation().getCell().getSector().getDistrict().getDistrictCode());

                        if (farmer.getLocation().getCell().getSector().getDistrict().getProvince() != null) {
                            dto.setProvince(farmer.getLocation().getCell().getSector().getDistrict().getProvince().getProvinceName());
                            dto.setProvinceCode(farmer.getLocation().getCell().getSector().getDistrict().getProvince().getProvinceCode());
                        }
                    }
                }
            }
        }
        return dto;
    }
}

