package auca.ac.rw.AgriStock1.services;

import auca.ac.rw.AgriStock1.model.*;
import auca.ac.rw.AgriStock1.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final SectorRepository sectorRepository;
    private final CellRepository cellRepository;
    private final VillageRepository villageRepository;

    @Override
    public void run(String... args) throws Exception {
        if (provinceRepository.count() == 0) {
            initializeRwandaLocations();
        }
    }

    private void initializeRwandaLocations() {
        System.out.println("Initializing Rwanda location data...");

        // ==================== KIGALI PROVINCE ====================
        Province kigali = new Province();
        kigali.setProvinceCode("KG");
        kigali.setProvinceName("Kigali");
        kigali = provinceRepository.save(kigali);

        // Gasabo District
        District gasabo = createDistrict("GASABO", "Gasabo", kigali);

        Sector remera = createSector("REMERA", "Remera", gasabo);
        Cell gisimenti = createCell("GISIMENTI", "Gisimenti", remera);
        createVillage("GISI001", "Gisimenti Village", gisimenti);
        createVillage("GISI002", "Kibagabaga", gisimenti);

        Sector kimironko = createSector("KIMIRONKO", "Kimironko", gasabo);
        Cell biryogo = createCell("BIRYOGO", "Biryogo", kimironko);
        createVillage("BIR001", "Biryogo I", biryogo);
        createVillage("BIR002", "Biryogo II", biryogo);

        // Kicukiro District
        District kicukiro = createDistrict("KICUKIRO", "Kicukiro", kigali);
        Sector niboye = createSector("NIBOYE", "Niboye", kicukiro);
        Cell kagarama = createCell("KAGARAMA", "Kagarama", niboye);
        createVillage("KAG001", "Kagarama Village", kagarama);

        // Nyarugenge District
        District nyarugenge = createDistrict("NYARUGENGE", "Nyarugenge", kigali);
        Sector nyarugenge_sector = createSector("NYARUGENGE", "Nyarugenge", nyarugenge);
        Cell rwezamenyo = createCell("RWEZAMENYO", "Rwezamenyo", nyarugenge_sector);
        createVillage("RWE001", "Rwezamenyo Village", rwezamenyo);

        // ==================== EASTERN PROVINCE ====================
        Province eastern = new Province();
        eastern.setProvinceCode("EP");
        eastern.setProvinceName("Eastern");
        eastern = provinceRepository.save(eastern);

        // Rwamagana District
        District rwamagana = createDistrict("RWAMAGANA", "Rwamagana", eastern);
        Sector kigabiro = createSector("KIGABIRO", "Kigabiro", rwamagana);
        Cell karambi = createCell("KARAMBI", "Karambi", kigabiro);
        createVillage("KRM001", "Karambi Village", karambi);
        createVillage("KRM002", "Nyamugari", karambi);

        // Kayonza District
        District kayonza = createDistrict("KAYONZA", "Kayonza", eastern);
        Sector mukarange = createSector("MUKARANGE", "Mukarange", kayonza);
        Cell gahara = createCell("GAHARA", "Gahara", mukarange);
        createVillage("GAH001", "Gahara Village", gahara);

        // ==================== NORTHERN PROVINCE ====================
        Province northern = new Province();
        northern.setProvinceCode("NP");
        northern.setProvinceName("Northern");
        northern = provinceRepository.save(northern);

        // Musanze District
        District musanze = createDistrict("MUSANZE", "Musanze", northern);
        Sector muhoza = createSector("MUHOZA", "Muhoza", musanze);
        Cell rugengabari = createCell("RUGENGABARI", "Rugengabari", muhoza);
        createVillage("RUG001", "Rugengabari I", rugengabari);
        createVillage("RUG002", "Rugengabari II", rugengabari);

        // Gakenke District
        District gakenke = createDistrict("GAKENKE", "Gakenke", northern);
        Sector rushashi = createSector("RUSHASHI", "Rushashi", gakenke);
        Cell nyarurembo = createCell("NYARUREMBO", "Nyarurembo", rushashi);
        createVillage("NYR001", "Nyarurembo Village", nyarurembo);

        // ==================== SOUTHERN PROVINCE ====================
        Province southern = new Province();
        southern.setProvinceCode("SP");
        southern.setProvinceName("Southern");
        southern = provinceRepository.save(southern);

        // Huye District
        District huye = createDistrict("HUYE", "Huye", southern);
        Sector ngoma = createSector("NGOMA", "Ngoma", huye);
        Cell matyazo = createCell("MATYAZO", "Matyazo", ngoma);
        createVillage("MAT001", "Matyazo Village", matyazo);
        createVillage("MAT002", "Cyarwa", matyazo);

        // Muhanga District
        District muhanga = createDistrict("MUHANGA", "Muhanga", southern);
        Sector nyamabuye = createSector("NYAMABUYE", "Nyamabuye", muhanga);
        Cell burima = createCell("BURIMA", "Burima", nyamabuye);
        createVillage("BUR001", "Burima Village", burima);

        // ==================== WESTERN PROVINCE ====================
        Province western = new Province();
        western.setProvinceCode("WP");
        western.setProvinceName("Western");
        western = provinceRepository.save(western);

        // Rubavu District
        District rubavu = createDistrict("RUBAVU", "Rubavu", western);
        Sector gisenyi = createSector("GISENYI", "Gisenyi", rubavu);
        Cell umuganda = createCell("UMUGANDA", "Umuganda", gisenyi);
        createVillage("UMU001", "Umuganda Village", umuganda);
        createVillage("UMU002", "Gisenyi Beach", umuganda);

        // Rusizi District
        District rusizi = createDistrict("RUSIZI", "Rusizi", western);
        Sector kamembe = createSector("KAMEMBE", "Kamembe", rusizi);
        Cell kamembe_cell = createCell("KAMEMBE", "Kamembe", kamembe);
        createVillage("KMB001", "Kamembe Town", kamembe_cell);

        System.out.println("Rwanda location data initialized successfully!");
    }

    private District createDistrict(String code, String name, Province province) {
        District district = new District();
        district.setDistrictCode(code);
        district.setDistrictName(name);
        district.setProvince(province);
        return districtRepository.save(district);
    }

    private Sector createSector(String code, String name, District district) {
        Sector sector = new Sector();
        sector.setSectorCode(code);
        sector.setSectorName(name);
        sector.setDistrict(district);
        return sectorRepository.save(sector);
    }

    private Cell createCell(String code, String name, Sector sector) {
        Cell cell = new Cell();
        cell.setCellCode(code);
        cell.setCellName(name);
        cell.setSector(sector);
        return cellRepository.save(cell);
    }

    private Village createVillage(String code, String name, Cell cell) {
        Village village = new Village();
        village.setVillageCode(code);
        village.setVillageName(name);
        village.setCell(cell);
        return villageRepository.save(village);
    }
}