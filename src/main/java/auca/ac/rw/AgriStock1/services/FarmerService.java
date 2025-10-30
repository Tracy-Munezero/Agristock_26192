package auca.ac.rw.AgriStock1.services;

import auca.ac.rw.AgriStock1.model.Farmer;
import auca.ac.rw.AgriStock1.model.Village;
import auca.ac.rw.AgriStock1.repositories.FarmerRepository;
import auca.ac.rw.AgriStock1.repositories.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final VillageRepository villageRepository;

    // ==================== CREATE ====================
    public Farmer createFarmer(Farmer farmer) {
        // Validate email uniqueness
        if (farmerRepository.existsByEmail(farmer.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Validate location exists
        if (farmer.getLocation() != null && farmer.getLocation().getVillageId() != null) {
            Village village = villageRepository.findById(farmer.getLocation().getVillageId())
                    .orElseThrow(() -> new RuntimeException("Village not found"));
            farmer.setLocation(village);
        }

        return farmerRepository.save(farmer);
    }

    // ==================== READ ====================
    public Farmer getFarmerById(Long id) {
        return farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + id));
    }

    public List<Farmer> getAllFarmers() {
        return farmerRepository.findAll();
    }

    public Page<Farmer> getAllFarmersPaginated(Pageable pageable) {
        return farmerRepository.findAll(pageable);
    }

    public Farmer getFarmerByEmail(String email) {
        return farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found with email: " + email));
    }

    // ==================== UPDATE ====================
    public Farmer updateFarmer(Long id, Farmer farmerDetails) {
        Farmer farmer = getFarmerById(id);

        // Update fields
        farmer.setFirstName(farmerDetails.getFirstName());
        farmer.setLastName(farmerDetails.getLastName());
        farmer.setPhone(farmerDetails.getPhone());

        // Update email only if changed and not duplicate
        if (!farmer.getEmail().equals(farmerDetails.getEmail())) {
            if (farmerRepository.existsByEmail(farmerDetails.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            farmer.setEmail(farmerDetails.getEmail());
        }

        // Update location if provided
        if (farmerDetails.getLocation() != null) {
            Village village = villageRepository.findById(farmerDetails.getLocation().getVillageId())
                    .orElseThrow(() -> new RuntimeException("Village not found"));
            farmer.setLocation(village);
        }

        return farmerRepository.save(farmer);
    }

    // ==================== DELETE ====================
    public void deleteFarmer(Long id) {
        if (!farmerRepository.existsById(id)) {
            throw new RuntimeException("Farmer not found with id: " + id);
        }
        farmerRepository.deleteById(id);
    }

    // ==================== CUSTOM QUERIES ====================

    // Find farmers by province code
    public List<Farmer> getFarmersByProvinceCode(String provinceCode) {
        return farmerRepository.findByLocationCellSectorDistrictProvinceProvinceCode(provinceCode);
    }

    // Find farmers by province name
    public List<Farmer> getFarmersByProvinceName(String provinceName) {
        return farmerRepository.findByLocationCellSectorDistrictProvinceProvinceName(provinceName);
    }

    // Find farmers by district
    public List<Farmer> getFarmersByDistrictCode(String districtCode) {
        return farmerRepository.findByLocationCellSectorDistrictDistrictCode(districtCode);
    }

    // Find farmers by village
    public List<Farmer> getFarmersByVillageId(Long villageId) {
        return farmerRepository.findByLocationVillageId(villageId);
    }

    // Find farmers registered within date range
    public List<Farmer> getFarmersByRegistrationDateRange(LocalDate startDate, LocalDate endDate) {
        return farmerRepository.findByRegistrationDateBetween(startDate, endDate);
    }

    // Search farmers by name
    public List<Farmer> searchFarmersByName(String searchTerm) {
        return farmerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm
        );
    }

    // Check if email exists
    public boolean emailExists(String email) {
        return farmerRepository.existsByEmail(email);
    }
}
