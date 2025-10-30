package auca.ac.rw.AgriStock1.model.DTO;

public class LocationHierarchyDTO {
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
