package no.ding.pk.web.dto.converters;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import no.ding.pk.web.dto.sap.MaterialDTO;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SapMaterialDTODeserializer extends JsonDeserializer<MaterialDTO> {

//    public SapMaterialDTODeserializer() {
//        super();
//    }

    @Override
    public MaterialDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        MaterialDTO materialDTO = MaterialDTO.builder()
                .material(node.get("Material").asText())
                .salesOrganization(node.get("SalesOrganization").asText()) // 100"
                .distributionChannel(node.get("DistributionChannel").asText()) // 01" Alltid 01 (nedstrøms)
                .materialDescription(node.get("MaterialDescription").asText()) // Flatvogn - Utsett"
                .manufacturerMaterialNumber(node.get("ManufacturerMaterialNumber").asText()) // "
                .materialGroup(node.get("MaterialGroup").asText()) // 0503"
                .materialGroupDescription(node.get("MaterialGroupDescription").asText()) // Tj.  Flatvogn"
                .materialType(node.get("MaterialType").asText()) // DIEN"
                .materialTypeDescription(node.get("MaterialTypeDescription").asText()) // Tjeneste"
                .accountingRequirement(node.get("AccountingRequirement").asText()) // ZO"
                .materialExpired(node.get("MaterialExpired").asBoolean()) // "
                .volume(node.get("Volume").asDouble()) // 0.000
                .volumeUnit(node.get("VolumeUnit").asText()) // "
                .netWeight(node.get("NetWeight").asDouble()) // 0.000
                .weightUnit(node.get("WeightUnit").asText()) // KG"
                .dimensions(node.get("Dimensions").asText()) // "
                .depth(node.get("Depth").asDouble()) // 0.000
                .width(node.get("Width").asDouble()) // 0.000
                .height(node.get("Height").asDouble()) // 0.000
                .dimensionUnit(node.get("DimensionUnit").asText()) // "
                .maxLoad(node.get("MaxLoad").asDouble()) // 0.000
                .maxLoadUnit(node.get("MaxLoadUnit").asText()) // "
                .categoryId(node.get("CategoryId").asText()) // "
                .categoryDescription(node.get("CategoryDescription").asText()) // "
                .subCategoryId(node.get("SubCategoryId").asText()) // "
                .subCategoryDescription(node.get("SubCategoryDescription").asText()) // "
                .classId(node.get("ClassId").asText()) // "
                .classDescription(node.get("ClassDescription").asText()) // "
                .lastChangedTimestamp(node.get("LastChangedTimestamp").asText()) // 20220126093547
                .build();

        String lastChangedTime = node.get("LastChangedTime").asText();
        String lastChangedDate = node.get("LastChangedDate").asText();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss-yyyy-MM-dd");
        Date lastChangedDateInstance = null;
        try {
            lastChangedDateInstance = sdf.parse(lastChangedTime + "-" + lastChangedDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if(lastChangedDateInstance != null) {
            materialDTO.setLastChangedDate(lastChangedDateInstance);
        }

        return materialDTO;
    }
}
