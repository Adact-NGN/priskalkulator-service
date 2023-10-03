package no.ding.pk.web.mappings;

import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import no.ding.pk.domain.offer.template.TemplateMaterial;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.web.dto.v2.web.client.offer.template.PriceOfferTemplateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@Import(ModelMapperV2Config.class)
public class PriceOfferTemplateMappingTest {

    private ModelMapper modelMapper;
    private SalesRoleRepository salesRoleRepository;
    private MaterialService materialService;

    @BeforeEach
    public void setup() {
        salesRoleRepository = mock(SalesRoleRepository.class);
        materialService = mock(MaterialService.class);

        modelMapper = new ModelMapperV2Config().modelMapperV2(materialService, salesRoleRepository);
    }

    @Test
    public void shouldMapPriceOfferToDTO() {
        PriceOfferTemplate priceOfferTemplate = createCompleteOfferTemplate();

        PriceOfferTemplateDTO actual = modelMapper.map(priceOfferTemplate, PriceOfferTemplateDTO.class);

        assertThat(actual.getAuthor(), equalTo(priceOfferTemplate.getAuthor().getEmail()));
        assertThat(actual.getSharedWith().get(0), equalTo(priceOfferTemplate.getSharedWith().get(0).getEmail()));
    }

    private PriceOfferTemplate createCompleteOfferTemplate() {
        User salesEmployee = createEmployee();

        User sharedWithUser = User.builder()
                .adId("ad-ww-wegarijo-arha-rh-arha")
                .associatedPlace("Oslo")
                .email("alexander.brox@ngn.no")
                .department("Salg")
                .fullName("Alexander Brox")
                .name("Alexander")
                .sureName("Brox")
                .jobTitle("Markedskonsulent")
                .build();

        TemplateMaterial material = TemplateMaterial.builder()
                .material("50101")
                .build();

        TemplateMaterial waste = TemplateMaterial.builder()
                .material("119901")
                .build();


        return PriceOfferTemplate.builder()
                .author(salesEmployee)
                .materials(List.of(material, waste))
                .isShareable(true)
                .sharedWith(List.of(sharedWithUser))
                .build();
    }

    private User createEmployee() {
        User salesEmployee = User.builder()
                .adId("ad-id-wegarijo-arha-rh-arha")
                .associatedPlace("Larvik")
                .email("Wolfgang@farris-bad.no")
                .department("Hvitsnippene")
                .fullName("Wolfgang Amadeus Mozart")
                .jobTitle("Komponist")
                .build();

        return salesEmployee;
    }
}
