package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Farmer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    // Find farmers by province code (complex nested query)
    List<Farmer> findByLocationCellSectorDistrictProvinceProvinceCode(String provinceCode);

    // Find farmers by province name
    List<Farmer> findByLocationCellSectorDistrictProvinceProvinceName(String provinceName);

    // Find farmers by district
    List<Farmer> findByLocationCellSectorDistrictDistrictCode(String districtCode);

    // Find farmers by village
    List<Farmer> findByLocationVillageId(Long villageId);

    // Email validation
    boolean existsByEmail(String email);
    Optional<Farmer> findByEmail(String email);

    // Pagination
    Page<Farmer> findAll(Pageable pageable);

    // Date range queries
    List<Farmer> findByRegistrationDateBetween(LocalDate startDate, LocalDate endDate);
    List<Farmer> findByRegistrationDateAfter(LocalDate date);

    // Search by name
    List<Farmer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName
    );
}
