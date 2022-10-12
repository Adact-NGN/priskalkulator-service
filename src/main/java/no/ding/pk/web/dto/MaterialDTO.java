package no.ding.pk.web.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(value = { "__metadata" })
public class MaterialDTO  {
    @JsonDeserialize(converter = LongToDateConverter.class)
    @JsonAlias("Gyldigfra") //: "/Date(1663027200000)/",
    private Date validFrom;
    @JsonAlias("Salgsorg") //: "100",
    private String salesOrg;
    @JsonAlias("Salgskontor") //: "104",
    private String salesOffice;
    @JsonAlias("Material") //: "50000",
    private String material;
    @JsonAlias("Betegnelse") //: "Mal for tjenester",
    private String designation;
    @JsonAlias("Apparattype") //: "",
    private String deviceType;
    @JsonAlias("Sone") //: "",
    private String zone;
    @JsonAlias("Skalakvantum") //: "0.000",
    private String scaleQuantum;
    @JsonAlias("StandardPris") //: "0.00",
    private String standardPrice;
    @JsonAlias("Valuta") //: "",
    private String currency;
    @JsonAlias("Prisenhet") //: "1000",
    private String priceUnit;
    @JsonAlias("Kvantumsenhet") //: "KG",
    private String quantumUnit;
    @JsonDeserialize(converter = LongToDateConverter.class)
    @JsonAlias("Gyldigtil") //: "/Date(253402214400000)/",
    private Date validTo;

    @JsonAlias("Varegruppe") //: "0501",
    private String productGroup;
    @JsonAlias("Varegruppebetegnelse") //: "Tj. Lift",
    private String productGroupDesignation;
    @JsonAlias("Materialtype") //: "DIEN",
    private String materialType;
    @JsonAlias("Materialbeskrivelse") //: "Tjeneste"
    private String materialDesignation;
    
    public Date getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
    public String getSalesOrg() {
        return salesOrg;
    }
    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }
    public String getSalesOffice() {
        return salesOffice;
    }
    public void setSalesOffice(String salesOffice) {
        this.salesOffice = salesOffice;
    }
    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getZone() {
        return zone;
    }
    public void setZone(String zone) {
        this.zone = zone;
    }
    public String getScaleQuantum() {
        return scaleQuantum;
    }
    public void setScaleQuantum(String scaleQuantum) {
        this.scaleQuantum = scaleQuantum;
    }
    public String getStandardPrice() {
        return standardPrice;
    }
    public void setStandardPrice(String standardPrice) {
        this.standardPrice = standardPrice;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getPriceUnit() {
        return priceUnit;
    }
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }
    public String getQuantumUnit() {
        return quantumUnit;
    }
    public void setQuantumUnit(String quantumUnit) {
        this.quantumUnit = quantumUnit;
    }
    public Date getValidTo() {
        return validTo;
    }
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }
    public String getProductGroup() {
        return productGroup;
    }
    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }
    public String getProductGroupDesignation() {
        return productGroupDesignation;
    }
    public void setProductGroupDesignation(String productGroupDesignation) {
        this.productGroupDesignation = productGroupDesignation;
    }
    public String getMaterialType() {
        return materialType;
    }
    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }
    public String getMaterialDesignation() {
        return materialDesignation;
    }
    public void setMaterialDesignation(String materialDesignation) {
        this.materialDesignation = materialDesignation;
    }

    
}
