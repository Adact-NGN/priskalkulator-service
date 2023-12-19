package no.ding.pk.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import no.ding.pk.web.dto.sap.MaterialDTO;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class LocalJsonUtilsTest {

    @Test
    public void shouldMapSapMaterialResponse() throws IOException {
        String responseFileName = "sap_material_response/sapMaterialResponse.json";

        ClassLoader classLoader = LocalJsonUtilsTest.class.getClassLoader();

        File file = new File(classLoader.getResource(responseFileName).getFile());

        assertThat(file.exists(), is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        JSONObject jsonObject = new JSONObject(json);

        JSONArray jsonArray = jsonObject.getJSONArray("value");

        List<MaterialDTO> materialDTOS = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject temp = jsonArray.getJSONObject(0);

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectReader objectReader = objectMapper.reader();

            MaterialDTO jsonNode = objectReader.readValue(temp.toString(), MaterialDTO.class);

            materialDTOS.add(jsonNode);
        }

        assertThat(materialDTOS, hasSize(1));
    }
}
