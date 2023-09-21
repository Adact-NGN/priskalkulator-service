package no.ding.pk.service;

import no.ding.pk.config.SchedulingTestConfig;
import no.ding.pk.config.mapping.v2.ModelMapperV2Config;
import no.ding.pk.repository.SalesOfficePowerOfAttorneyRepository;
import no.ding.pk.repository.bo.ConditionCodeRepository;
import no.ding.pk.repository.bo.KeyCombinationRepository;
import no.ding.pk.repository.offer.PriceOfferTermsRepository;
import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.sap.SapMaterialService;
import no.ding.pk.utils.LocalJSONUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.PropertyResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@Disabled("Cache is out of order")
@Tag("unittest")
@ActiveProfiles({ "test", "unit-test" })
@TestPropertySource(properties = {"cache.max.amount.items=5000"})
@SpringJUnitConfig({SchedulingTestConfig.class, ModelMapperV2Config.class})
public class MaterialInMemory3DCacheTest {

    @MockBean
    private SalesOfficePowerOfAttorneyRepository sopoaRepository;

    @MockBean
    private LocalJSONUtils localJSONUtils;

    @MockBean
    private ConditionCodeRepository conditionCodeRepository;

    @MockBean
    private KeyCombinationRepository keyCombinationRepository;

    @MockBean
    private PriceOfferTermsRepository priceOfferTermsRepository;

    @MockBean
    private SapMaterialService sapMaterialService;

    @MockBean
    private KieContainer kieContainer;

    @Autowired
    private InMemory3DCache<String, String, String> cache;

    @Qualifier("environment")
    @Autowired private PropertyResolver propertySourceResolver;

    @Test
    public void shouldAddAndRemoveObjects() {
        cache.put("Ansatt", "kjetil", "kjetil.torvund.minde@ngn.no");
        cache.put("Ansatt", "eirik", "eirik.flaa@ngn.no");
        cache.put("Ansatt", "jørgen", "jorgensq@ngn.no");
        cache.put("Merkevare", "Microsoft", "Microsoft");
        cache.put("Merkevare", "NGN", "NGN");
        cache.put("Merkevare", "Nintendo", "Nintendo");

        System.out.println("3 Cache Object of 'Ansatt' Added.. cache.size(): " + cache.size("Ansatt"));
        System.out.println("3 Cache Object of 'Merkevare' Added.. cache.size(): " + cache.size("Merkevare"));
        cache.remove("Merkevare", "IBM");
        System.out.println("One object of 'Merkevare' removed.. cache.size(): " + cache.size("Merkevare"));
        assertThat(cache.contains("Merkevare", "IBM"), is(false));

        cache.put("Merkevare", "PS", "PS");
        cache.put("Merkevare", "Xbox", "Xbox");
        System.out.println("Two objects added to 'Merkevare'.. cache.size(): " + cache.size("Merkevare"));
        assertThat(cache.size("Merkevare"), is(5));
    }

    @Test
    public void shouldGetAllInList() {
        cache.put("Ansatt", "kjetil", "kjetil.torvund.minde@ngn.no");
        cache.put("Ansatt", "eirik", "eirik.flaa@ngn.no");
        cache.put("Ansatt", "jørgen", "jorgensq@ngn.no");
        cache.put("Merkevare", "Microsoft", "Microsoft");
        cache.put("Merkevare", "NGN", "NGN");
        cache.put("Merkevare", "Nintendo", "Nintendo");

        List<String> ansattList = cache.getAllInList("Ansatt");

        assertThat(ansattList, hasSize(3));

        List<String> selectedAnsattList = cache.getAllInList("Ansatt", List.of("kjetil", "eirik"));

        assertThat(selectedAnsattList, hasSize(2));
    }
}
