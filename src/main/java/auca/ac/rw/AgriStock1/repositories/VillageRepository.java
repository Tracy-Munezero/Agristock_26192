package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Cell;
import auca.ac.rw.AgriStock1.model.Village;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {
    List<Village> findByCellCellCode(String cellCode);
    List<Village> findByCell(Cell cell);
    Optional<Village> findByVillageCode(String villageCode);
}