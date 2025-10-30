package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByFarmerFarmerId(Long farmerId);
    List<Inventory> findByTotalProductsGreaterThan(Integer threshold);
}
