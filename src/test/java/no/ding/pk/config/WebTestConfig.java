package no.ding.pk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.config.mapping.v1.ModelMapperConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.repository.DiscountLevelRepository;
import no.ding.pk.repository.DiscountRepository;
import no.ding.pk.repository.SalesRoleRepository;
import no.ding.pk.repository.UserRepository;
import no.ding.pk.repository.offer.MaterialPriceRepository;
import no.ding.pk.repository.offer.PriceRowRepository;
import no.ding.pk.repository.offer.SalesOfficeRepository;
import no.ding.pk.repository.offer.ZoneRepository;
import no.ding.pk.service.*;
import no.ding.pk.service.bo.BoReportConditionCodeService;
import no.ding.pk.service.converters.PdfService;
import no.ding.pk.service.offer.CustomerTermsService;
import no.ding.pk.service.offer.MaterialService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.service.offer.PriceOfferTemplateService;
import no.ding.pk.service.sap.*;
import no.ding.pk.service.template.HandlebarsTemplateService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.persistence.EntityManagerFactory;

@Configuration
@Import({ModelMapperConfig.class, ModelMapperV2Config.class})
@ComponentScan("no.ding.pk.web")
public class WebTestConfig implements WebMvcConfigurer {

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private BoReportConditionCodeService boReportConditionCodeService;

    @MockBean
    private UserAzureAdService userAzureAdService;

    @MockBean
    private ContactPersonService contactPersonService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerTermsService customerTermsService;

    @MockBean
    private DiscountService discountService;

    @MockBean
    private HandlebarsTemplateService handlebarsTemplateService;

    @MockBean
    private MaterialService materialService;

    @MockBean
    private SalesOrgService salesOrgService;

    @MockBean
    private SalesRoleService salesRoleService;

    @MockBean
    private StandardPriceService standardPriceService;

    @MockBean
    private SalesOfficePowerOfAttorneyService salesOfficePowerOfAttorneyService;

    @MockBean
    private SapMaterialService sapMaterialService;

    @MockBean
    private PdfService pdfService;

    @MockBean
    private PriceOfferService priceOfferService;

    @MockBean
    private PriceOfferTemplateService priceOfferTemplateService;

    @MockBean
    private UserService userService;

    @MockBean
    private DiscountLevelRepository discountLevelRepository;

    @MockBean
    private DiscountRepository discountRepository;

    @MockBean
    private SalesRoleRepository salesRoleRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SalesOfficeRepository salesOfficeRepository;

    @MockBean
    private PriceRowRepository priceRowRepository;

    @MockBean
    private MaterialPriceRepository materialPriceRepository;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private ZoneRepository zoneRepository;

    @MockBean
    private SapPricingService sapPricingService;
}
