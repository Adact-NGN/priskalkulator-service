package no.ding.pk.web.dto.sap;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.ding.pk.web.dto.converters.StringToBooleanConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MaterialDTO {
    @JsonAlias("Material")
    private String material; // 50301"
    @JsonAlias("SalesOrganization")
    private String salesOrganization; // 100"
    @JsonAlias("DistributionChannel")
    private String distributionChannel; // 01" Alltid 01 (nedstrøms)
    @JsonAlias("MaterialDescription")
    private String materialDescription; // Flatvogn - Utsett"
    @JsonAlias("ManufacturerMaterialNumber")
    private String manufacturerMaterialNumber; // "
    @JsonAlias("MaterialGroup")
    private String materialGroup; // 0503"
    @JsonAlias("MaterialGroupDescription")
    private String materialGroupDescription; // Tj.  Flatvogn"
    @JsonAlias("MaterialType")
    private String materialType; // DIEN"
    @JsonAlias("MaterialTypeDescription")
    private String materialTypeDescription; // Tjeneste"
    @JsonAlias("AccountingRequirement")
    private String accountingRequirement; // ZO"
    @JsonDeserialize(converter = StringToBooleanConverter.class)
    @JsonAlias("MaterialExpired")
    private boolean materialExpired; // "
    @JsonAlias("Volume")
    private Double volume; // 0.000
    @JsonAlias("VolumeUnit")
    private String volumeUnit; // "
    @JsonAlias("NetWeight")
    private Double netWeight; // 0.000
    @JsonAlias("WeightUnit")
    private String weightUnit; // KG"
    @JsonAlias("Dimensions")
    private String dimensions; // "
    @JsonAlias("Depth")
    private Double depth; // 0.000
    @JsonAlias("Width")
    private Double width; // 0.000
    @JsonAlias("Height")
    private Double height; // 0.000
    @JsonAlias("DimensionUnit")
    private String dimensionUnit; // "
    @JsonAlias("MaxLoad")
    private Double maxLoad; // 0.000
    @JsonAlias("MaxLoadUnit")
    private String maxLoadUnit; // "
    @JsonAlias("CategoryId")
    private String categoryId; // "
    @JsonAlias("CategoryDescription")
    private String categoryDescription; // "
    @JsonAlias("SubCategoryId")
    private String subCategoryId; // "
    @JsonAlias("SubCategoryDescription")
    private String subCategoryDescription; // "
    @JsonAlias("ClassId")
    private String classId; // "
    @JsonAlias("ClassDescription")
    private String classDescription; // "
    @JsonAlias("LastChangedTimestamp")
    private String lastChangedTimestamp; // 20220126093547
    @JsonAlias("LastChangedTime")
    private String lastChangedTime;
    @JsonAlias("LastChangedDate")
    private Date lastChangedDate; // 2022-01-26"

    // @JsonAlias("MaterialStdPrice")
    // private List<MaterialStdPriceDTO> materialStdPrices;

    // public void addMaterialStdPrice(MaterialStdPriceDTO materialStdPriceDTO) {
    //     if(materialStdPrices == null) {
    //         materialStdPrices = new ArrayList<>();
    //     }

    //     materialStdPrices.add(materialStdPriceDTO);
    // }

    // public void addAllMaterialStdPrices(List<MaterialStdPriceDTO> stdPrices) {
    //     if(materialStdPrices == null) {
    //         materialStdPrices = new ArrayList<>();
    //     }

    //     materialStdPrices.addAll(stdPrices);
    // }

}
