package auca.ac.rw.AgriStock1.repositories;

import auca.ac.rw.AgriStock1.model.Cell;
import auca.ac.rw.AgriStock1.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CellRepository extends JpaRepository<Cell, Long> {
    List<Cell> findBySectorSectorCode(String sectorCode);
}