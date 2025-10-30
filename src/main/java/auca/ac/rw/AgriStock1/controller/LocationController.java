package auca.ac.rw.AgriStock1.controller;

import auca.ac.rw.AgriStock1.model.*;
import auca.ac.rw.AgriStock1.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LocationController {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final SectorRepository sectorRepository;
    private final CellRepository cellRepository;
    private final VillageRepository villageRepository;

    // ==================== PROVINCE ENDPOINTS ====================

    @GetMapping("/provinces")
    public ResponseEntity<List<Province>> getAllProvinces() {
        List<Province> provinces = provinceRepository.findAll();
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/provinces/{id}")
    public ResponseEntity<Province> getProvinceById(@PathVariable Long id) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Province not found"));
        return ResponseEntity.ok(province);
    }

    @GetMapping("/provinces/code/{code}")
    public ResponseEntity<Province> getProvinceByCode(@PathVariable String code) {
        Province province = provinceRepository.findByProvinceCode(code)
                .orElseThrow(() -> new RuntimeException("Province not found"));
        return ResponseEntity.ok(province);
    }

    @GetMapping("/provinces/name/{name}")
    public ResponseEntity<Province> getProvinceByName(@PathVariable String name) {
        Province province = provinceRepository.findByProvinceName(name)
                .orElseThrow(() -> new RuntimeException("Province not found"));
        return ResponseEntity.ok(province);
    }

    // ==================== DISTRICT ENDPOINTS ====================

    @GetMapping("/districts")
    public ResponseEntity<List<District>> getAllDistricts() {
        List<District> districts = districtRepository.findAll();
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/districts/{id}")
    public ResponseEntity<District> getDistrictById(@PathVariable Long id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("District not found"));
        return ResponseEntity.ok(district);
    }

    @GetMapping("/provinces/{provinceCode}/districts")
    public ResponseEntity<List<District>> getDistrictsByProvince(@PathVariable String provinceCode) {
        List<District> districts = districtRepository.findByProvinceProvinceCode(provinceCode);
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/districts/code/{code}")
    public ResponseEntity<District> getDistrictByCode(@PathVariable String code) {
        District district = districtRepository.findByDistrictCode(code)
                .orElseThrow(() -> new RuntimeException("District not found"));
        return ResponseEntity.ok(district);
    }

    // ==================== SECTOR ENDPOINTS ====================

    @GetMapping("/sectors")
    public ResponseEntity<List<Sector>> getAllSectors() {
        List<Sector> sectors = sectorRepository.findAll();
        return ResponseEntity.ok(sectors);
    }

    @GetMapping("/sectors/{id}")
    public ResponseEntity<Sector> getSectorById(@PathVariable Long id) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sector not found"));
        return ResponseEntity.ok(sector);
    }

    @GetMapping("/districts/{districtCode}/sectors")
    public ResponseEntity<List<Sector>> getSectorsByDistrict(@PathVariable String districtCode) {
        List<Sector> sectors = sectorRepository.findByDistrictDistrictCode(districtCode);
        return ResponseEntity.ok(sectors);
    }

    // ==================== CELL ENDPOINTS ====================

    @GetMapping("/cells")
    public ResponseEntity<List<Cell>> getAllCells() {
        List<Cell> cells = cellRepository.findAll();
        return ResponseEntity.ok(cells);
    }

    @GetMapping("/cells/{id}")
    public ResponseEntity<Cell> getCellById(@PathVariable Long id) {
        Cell cell = cellRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cell not found"));
        return ResponseEntity.ok(cell);
    }

    @GetMapping("/sectors/{sectorCode}/cells")
    public ResponseEntity<List<Cell>> getCellsBySector(@PathVariable String sectorCode) {
        List<Cell> cells = cellRepository.findBySectorSectorCode(sectorCode);
        return ResponseEntity.ok(cells);
    }

    // ==================== VILLAGE ENDPOINTS ====================

    @GetMapping("/villages")
    public ResponseEntity<List<Village>> getAllVillages() {
        List<Village> villages = villageRepository.findAll();
        return ResponseEntity.ok(villages);
    }

    @GetMapping("/villages/{id}")
    public ResponseEntity<Village> getVillageById(@PathVariable Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Village not found"));
        return ResponseEntity.ok(village);
    }

    @GetMapping("/cells/{cellCode}/villages")
    public ResponseEntity<List<Village>> getVillagesByCell(@PathVariable String cellCode) {
        List<Village> villages = villageRepository.findByCellCellCode(cellCode);
        return ResponseEntity.ok(villages);
    }

    @GetMapping("/villages/code/{code}")
    public ResponseEntity<Village> getVillageByCode(@PathVariable String code) {
        Village village = villageRepository.findByVillageCode(code)
                .orElseThrow(() -> new RuntimeException("Village not found"));
        return ResponseEntity.ok(village);
    }

    // ==================== HIERARCHICAL QUERIES ====================

    @GetMapping("/hierarchy/village/{villageId}")
    public ResponseEntity<LocationHierarchyDTO> getLocationHierarchy(@PathVariable Long villageId) {
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new RuntimeException("Village not found"));

        LocationHierarchyDTO hierarchy = new LocationHierarchyDTO();
        hierarchy.setVillage(village.getVillageName());
        hierarchy.setVillageCode(village.getVillageCode());

        if (village.getCell() != null) {
            hierarchy.setCell(village.getCell().getCellName());
            hierarchy.setCellCode(village.getCell().getCellCode());

            if (village.getCell().getSector() != null) {
                hierarchy.setSector(village.getCell().getSector().getSectorName());
                hierarchy.setSectorCode(village.getCell().getSector().getSectorCode());

                if (village.getCell().getSector().getDistrict() != null) {
                    hierarchy.setDistrict(village.getCell().getSector().getDistrict().getDistrictName());
                    hierarchy.setDistrictCode(village.getCell().getSector().getDistrict().getDistrictCode());

                    if (village.getCell().getSector().getDistrict().getProvince() != null) {
                        hierarchy.setProvince(village.getCell().getSector().getDistrict().getProvince().getProvinceName());
                        hierarchy.setProvinceCode(village.getCell().getSector().getDistrict().getProvince().getProvinceCode());
                    }
                }
            }
        }

        return ResponseEntity.ok(hierarchy);
    }
}

// ==================== DTO CLASS ====================
class LocationHierarchyDTO {
    private String province;
    private String provinceCode;
    private String district;
    private String districtCode;
    private String sector;
    private String sectorCode;
    private String cell;
    private String cellCode;
    private String village;
    private String villageCode;

    // Getters and Setters
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getProvinceCode() { return provinceCode; }
    public void setProvinceCode(String provinceCode) { this.provinceCode = provinceCode; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDistrictCode() { return districtCode; }
    public void setDistrictCode(String districtCode) { this.districtCode = districtCode; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getSectorCode() { return sectorCode; }
    public void setSectorCode(String sectorCode) { this.sectorCode = sectorCode; }

    public String getCell() { return cell; }
    public void setCell(String cell) { this.cell = cell; }

    public String getCellCode() { return cellCode; }
    public void setCellCode(String cellCode) { this.cellCode = cellCode; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getVillageCode() { return villageCode; }
    public void setVillageCode(String villageCode) { this.villageCode = villageCode; }
}