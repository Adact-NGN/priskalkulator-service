package no.ding.pk.web.mappings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import no.ding.pk.web.dto.web.client.offer.PriceOfferListDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
public class PriceOfferListDtoMappingTest {

    @Autowired
    @Qualifier(value = "modelMapperV2") private ModelMapper modelMapper;

    @Test
    public void shouldMapPriceOfferToPriceOfferListDTO() {
        PriceOfferDTO priceOfferDTO = createCompleteOfferDto();

        PriceOffer priceOffer = modelMapper.map(priceOfferDTO, PriceOffer.class);

        PriceOfferListDTO priceOfferListDTO = modelMapper.map(priceOffer, PriceOfferListDTO.class);

        assertThat(priceOfferListDTO.getSalesEmployee(), notNullValue());
        assertThat(priceOfferListDTO.getSalesEmployee().getFullName(), notNullValue());
    }

    private PriceOfferDTO createCompleteOfferDto() {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(Objects.requireNonNull(classLoader.getResource("priceOfferDtoV2.json")).getFile());

        assertThat(file.exists(), is(true));

        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);

        String json;
        try {
            json = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(json, PriceOfferDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
