package no.ding.pk.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import org.apache.commons.io.IOUtils;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
}
