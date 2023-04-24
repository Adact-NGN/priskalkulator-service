package no.ding.pk.web.dto.sap;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.web.dto.converters.LongToDateConverter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(value = { "__metadata" })
public class MaterialStdPriceDTO {
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
    private Double scaleQuantum;
    @JsonAlias({"StandardPris", "StandardPrice"}) //: "0.00",
    private Double standardPrice;
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
    private String materialTypeDesignation;

    @JsonAlias({"MaterialData"})
    private MaterialDTO materialData;

    @JsonGetter(value = "formattedStandardPrice")
    public String getFormattedStandardPrice() {
        return this.standardPrice != null ? String.format("%.2f", this.standardPrice) : "";
    }

    @JsonGetter(value = "formattedScaleQuantum")
    public String getFormattedScaleQuantum() {
        return this.scaleQuantum != null ? String.format("%.3f", this.scaleQuantum) : "";
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
        result = prime * result + ((materialTypeDesignation == null) ? 0 : materialTypeDesignation.hashCode());
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
        MaterialStdPriceDTO other = (MaterialStdPriceDTO) obj;
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
        if (materialTypeDesignation == null) {
            if (other.materialTypeDesignation != null)
                return false;
        } else if (!materialTypeDesignation.equals(other.materialTypeDesignation))
            return false;
        return true;
    }
}
