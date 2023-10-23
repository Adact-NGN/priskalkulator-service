package no.ding.pk.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.ding.pk.web.dto.sap.MaterialDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JsonTestUtils {

    public static PriceOfferDTO createCompleteOfferDto(String filename) throws IOException {
        String inputFileName = filename;
        if (StringUtils.isBlank(filename)) {
            inputFileName = "priceOfferWithZoneAndDiscount_V2.json";
        }

        ClassLoader classLoader = JsonTestUtils.class.getClassLoader();

        File file = new File(Objects.requireNonNull(classLoader.getResource(inputFileName)).getFile());

        assertThat(file.exists(), is(true));

        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(json, PriceOfferDTO.class);
    }

    public static <T> T getResponseBody(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException {
        String jsonString = mvcResult.getResponse().getContentAsString();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
        return gson.fromJson(jsonString, clazz);
    }

    public static List<MaterialDTO> mockSapMaterialServiceResponse(ClassLoader classLoader) throws IOException {
        File file = new File(classLoader.getResource("materials100.json").getFile());

        assertThat(file.exists(), Is.is(true));

        String json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        JSONObject jsonObjectResult = new JSONObject(json);

        JSONArray result = jsonObjectResult.getJSONArray("value");

        ObjectMapper om = new ObjectMapper();

        List<MaterialDTO> materialDTOS = new ArrayList<>();

        for(int i = 0; i < result.length(); i++) {
            JSONObject jsonObject = result.getJSONObject(i);

            MaterialDTO materialDTO = om.readValue(jsonObject.toString(), MaterialDTO.class);

            materialDTOS.add(materialDTO);
        }

        return materialDTOS;
    }

    public static <T> String objectToJson(T object) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        ObjectWriter objectWriter = om.writer().withDefaultPrettyPrinter();

        return objectWriter.writeValueAsString(object);
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) throws JsonProcessingException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ObjectReader om = new ObjectMapper().readerForUpdating(clazz.getDeclaredConstructor().newInstance());

        return om.readValue(json);
    }
}
