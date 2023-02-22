package no.ding.pk.web.dto.converters;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import no.ding.pk.web.dto.sap.MaterialStdPriceDTO;

public class MaterialStdPriceDTODeserializer extends JsonDeserializer<MaterialStdPriceDTO> {
    
    @Override
    public MaterialStdPriceDTO deserialize(JsonParser p, DeserializationContext ctxt)
    throws IOException, JacksonException {
        
        JsonNode node = p.getCodec().readTree(p);
        
        MaterialStdPriceDTO materialStdPriceDTO = MaterialStdPriceDTO
        .builder()
        .salesOrg(null)
        .salesOffice(null)
        .material(null)
        .designation(null)
        .deviceType(null)
        .zone(null)
        .scaleQuantum(null)
        .standardPrice(null)
        .currency(null)
        .pricingUnit(null)
        .quantumUnit(null)
        .materialExpired(null)
        .validFrom(null)
        .validTo(null)
        .productGroup(null)
        .productGroupDesignation(null)
        .materialType(null)
        .materialTypeDesignation(null)
        // .materialData(null)
        .build();
        return materialStdPriceDTO;
    }
    
}
