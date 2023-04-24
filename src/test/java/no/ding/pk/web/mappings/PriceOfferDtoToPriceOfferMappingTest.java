package no.ding.pk.web.mappings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.web.dto.web.client.offer.PriceOfferDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringBootTest
public class PriceOfferDtoToPriceOfferMappingTest {

    @Autowired()
    @Qualifier(value = "modelMapperV2")
    private ModelMapper modelMapper;

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

    @Test
    public void shouldMapPriceOfferDtoToPriceOffer() {
        PriceOfferDTO testPriceOfferDto = createCompleteOfferDto();

        assertThat(testPriceOfferDto.getCustomerNumber(), notNullValue());
        assertThat(testPriceOfferDto.getCustomerName(), notNullValue());

        ModelMapper modelMapper1 = new ModelMapper();
        modelMapper1.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        PriceOffer priceOffer = modelMapper.map(testPriceOfferDto, PriceOffer.class);

        assertThat(priceOffer.getCustomerNumber(), notNullValue());
        assertThat(priceOffer.getCustomerName(), notNullValue());
        assertThat(priceOffer.getSalesOfficeList(), hasSize(greaterThan(0)));
        assertThat(priceOffer.getSalesEmployee(), notNullValue());
        assertThat(priceOffer.getCustomerTerms(), notNullValue());
    }
}
