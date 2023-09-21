package no.ding.pk.service.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import no.ding.pk.web.dto.converters.SapMaterialDTODeserializer;
import no.ding.pk.web.dto.sap.MaterialDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MapSapMaterialTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldMapSapMaterialToMaterialDTO() throws IOException, ParseException {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MaterialDTO.class, new SapMaterialDTODeserializer());
        objectMapper.registerModule(module);

        ClassLoader classLoader = getClass().getClassLoader();
        File sapMaterialJson = new File(Objects.requireNonNull(classLoader.getResource("sapMaterial.json")).getFile());

        assertThat(sapMaterialJson.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(sapMaterialJson), StandardCharsets.UTF_8);

        MaterialDTO materialDTO = objectMapper.readValue(json, MaterialDTO.class);

        assertThat(materialDTO.getMaterial(), equalTo("50101"));
        assertThat(materialDTO.getSalesOrganization(), equalTo("100"));
        assertThat(materialDTO.getDistributionChannel(), equalTo("01"));
        assertThat(materialDTO.getMaterialDescription(), equalTo("Lift - Utsett"));
        assertThat(materialDTO.getManufacturerMaterialNumber(), equalTo(""));
        assertThat(materialDTO.getMaterialGroup(), equalTo("0501"));
        assertThat(materialDTO.getMaterialGroupDescription(), equalTo("Tj. Lift"));
        assertThat(materialDTO.getMaterialType(), equalTo("DIEN"));
        assertThat(materialDTO.getMaterialTypeDescription(), equalTo("Tjeneste"));
        assertThat(materialDTO.getAccountingRequirement(), equalTo("ZQ"));
        assertThat(materialDTO.isMaterialExpired(), equalTo(false));
        assertThat(materialDTO.getVolume(), equalTo(0.000));
        assertThat(materialDTO.getVolumeUnit(), equalTo(""));
        assertThat(materialDTO.getNetWeight(), equalTo(0.000));
        assertThat(materialDTO.getWeightUnit(), equalTo("KG"));
        assertThat(materialDTO.getDimensions(), equalTo(""));
        assertThat(materialDTO.getDepth(), equalTo(0.000));
        assertThat(materialDTO.getWidth(), equalTo(0.000));
        assertThat(materialDTO.getHeight(), equalTo(0.000));
        assertThat(materialDTO.getDimensionUnit(), equalTo(""));
        assertThat(materialDTO.getMaxLoad(), equalTo(0.000));
        assertThat(materialDTO.getMaxLoadUnit(), equalTo(""));
        assertThat(materialDTO.getCategoryId(), equalTo("00200"));
        assertThat(materialDTO.getCategoryDescription(), equalTo("Transport"));
        assertThat(materialDTO.getSubCategoryId(), equalTo("0020000160"));
        assertThat(materialDTO.getSubCategoryDescription(), equalTo("Sone differensiert"));
        assertThat(materialDTO.getClassId(), equalTo(""));
        assertThat(materialDTO.getClassDescription(), equalTo(""));
        assertThat(materialDTO.getLastChangedTimestamp(), equalTo("20230104091243"));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss-yyyy-MM-dd");
        Date expectedDate = sdf.parse("10:12:43-2023-01-04");
        assertThat(materialDTO.getLastChangedDate(), equalTo(expectedDate));
    }
}
