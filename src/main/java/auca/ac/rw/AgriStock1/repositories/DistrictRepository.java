package auca.ac.rw.AgriStock1.repositories;
import auca.ac.rw.AgriStock1.model.District;
import auca.ac.rw.AgriStock1.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByProvinceProvinceCode(String provinceCode);
    List<District> findByProvince(Province province);
    Optional<District> findByDistrictCode(String districtCode);
}