package no.ding.pk.web.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = { "__metadata" })
public class MaterialDTO  {
    @JsonDeserialize(converter = LongToDateConverter.class)
    @JsonAlias({"Gyldigfra", "ValidFrom"}) //: "/Date(1663027200000)/",
    private Date validFrom;
    @JsonAlias({"Salgsorg", "SalesOrganization"}) //: "100",
    private String salesOrg;
    @JsonAlias({"Salgskontor", "SalesOffice"}) //: "104",
    private String salesOffice;
    @JsonAlias({"Material"}) //: "50000",
    private String material;
    @JsonAlias({"Betegnelse", "MaterialDescription"}) //: "Mal for tjenester",
    private String designation;
    @JsonAlias({"Apparattype", "DeviceCategory"}) //: "",
    private String deviceType;
    @JsonAlias({"Sone", "SalesZone"}) //: "",
    private String zone;
    @JsonAlias({"Skalakvantum", "ScaleQuantity"}) //: "0.000",
    private String scaleQuantum;
    @JsonAlias({"StandardPris", "StandardPrice"}) //: "0.00",
    private String standardPrice;
    @JsonAlias({"Valuta"}) //: "",
    private String currency;
    @JsonAlias({"Prisenhet", "PricingUnit"}) //: "1000",
    private String pricingUnit;
    @JsonAlias({"Kvantumsenhet", "QuantumUnit"}) //: "KG",
    private String quantumUnit;

    @JsonAlias({"MaterialExpired"})
    private String materialExpired;

    @JsonDeserialize(converter = LongToDateConverter.class)
    @JsonAlias({"Gyldigtil", "ValidTo"}) //: "/Date(253402214400000)/",
    private Date validTo;

    @JsonAlias({"Varegruppe", "MaterialGroup"}) //: "0501",
    private String productGroup;
    @JsonAlias({"Varegruppebetegnelse", "MaterialGroupDescription"}) //: "Tj. Lift",
    private String productGroupDesignation;
    @JsonAlias({"Materialtype", "MaterialType"}) //: "DIEN",
    private String materialType;
    @JsonAlias({"Materialbeskrivelse", "MaterialTypeDescription"}) //: "Tjeneste"
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
    public String getPricingUnit() {
        return pricingUnit;
    }
    public void setPricingUnit(String pricingUnit) {
        this.pricingUnit = pricingUnit;
    }
    public String getQuantumUnit() {
        return quantumUnit;
    }
    public void setQuantumUnit(String quantumUnit) {
        this.quantumUnit = quantumUnit;
    }

    public String getMaterialExpired() {
        return materialExpired;
    }
    public void setMaterialExpired(String materialExpired) {
        this.materialExpired = materialExpired;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
        result = prime * result + ((salesOrg == null) ? 0 : salesOrg.hashCode());
        result = prime * result + ((salesOffice == null) ? 0 : salesOffice.hashCode());
        result = prime * result + ((material == null) ? 0 : material.hashCode());
        result = prime * result + ((designation == null) ? 0 : designation.hashCode());
        result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
        result = prime * result + ((zone == null) ? 0 : zone.hashCode());
        result = prime * result + ((scaleQuantum == null) ? 0 : scaleQuantum.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((pricingUnit == null) ? 0 : pricingUnit.hashCode());
        result = prime * result + ((quantumUnit == null) ? 0 : quantumUnit.hashCode());
        result = prime * result + ((validTo == null) ? 0 : validTo.hashCode());
        result = prime * result + ((productGroup == null) ? 0 : productGroup.hashCode());
        result = prime * result + ((productGroupDesignation == null) ? 0 : productGroupDesignation.hashCode());
        result = prime * result + ((materialType == null) ? 0 : materialType.hashCode());
        result = prime * result + ((materialDesignation == null) ? 0 : materialDesignation.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MaterialDTO other = (MaterialDTO) obj;
        if (validFrom == null) {
            if (other.validFrom != null)
                return false;
        } else if (!validFrom.equals(other.validFrom))
            return false;
        if (salesOrg == null) {
            if (other.salesOrg != null)
                return false;
        } else if (!salesOrg.equals(other.salesOrg))
            return false;
        if (salesOffice == null) {
            if (other.salesOffice != null)
                return false;
        } else if (!salesOffice.equals(other.salesOffice))
            return false;
        if (material == null) {
            if (other.material != null)
                return false;
        } else if (!material.equals(other.material))
            return false;
        if (designation == null) {
            if (other.designation != null)
                return false;
        } else if (!designation.equals(other.designation))
            return false;
        if (deviceType == null) {
            if (other.deviceType != null)
                return false;
        } else if (!deviceType.equals(other.deviceType))
            return false;
        if (zone == null) {
            if (other.zone != null)
                return false;
        } else if (!zone.equals(other.zone))
            return false;
        if (scaleQuantum == null) {
            if (other.scaleQuantum != null)
                return false;
        } else if (!scaleQuantum.equals(other.scaleQuantum))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (pricingUnit == null) {
            if (other.pricingUnit != null)
                return false;
        } else if (!pricingUnit.equals(other.pricingUnit))
            return false;
        if (quantumUnit == null) {
            if (other.quantumUnit != null)
                return false;
        } else if (!quantumUnit.equals(other.quantumUnit))
            return false;
        if (validTo == null) {
            if (other.validTo != null)
                return false;
        } else if (!validTo.equals(other.validTo))
            return false;
        if (productGroup == null) {
            if (other.productGroup != null)
                return false;
        } else if (!productGroup.equals(other.productGroup))
            return false;
        if (productGroupDesignation == null) {
            if (other.productGroupDesignation != null)
                return false;
        } else if (!productGroupDesignation.equals(other.productGroupDesignation))
            return false;
        if (materialType == null) {
            if (other.materialType != null)
                return false;
        } else if (!materialType.equals(other.materialType))
            return false;
        if (materialDesignation == null) {
            if (other.materialDesignation != null)
                return false;
        } else if (!materialDesignation.equals(other.materialDesignation))
            return false;
        return true;
    }
}
