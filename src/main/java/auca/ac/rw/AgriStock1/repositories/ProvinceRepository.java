package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    Optional<Province> findByProvinceCode(String provinceCode);
    Optional<Province> findByProvinceName(String provinceName);
    boolean existsByProvinceCode(String provinceCode);
}