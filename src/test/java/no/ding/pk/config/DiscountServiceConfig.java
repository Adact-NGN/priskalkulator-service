package no.ding.pk.config;

import no.ding.pk.domain.Discount;
import no.ding.pk.repository.DiscountLevelRepository;
import no.ding.pk.repository.DiscountRepository;
import no.ding.pk.service.DiscountService;
import no.ding.pk.service.DiscountServiceImpl;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

@Profile("test")
@TestConfiguration
@TestPropertySource("/h2-db.properties")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DiscountServiceConfig {
    @Bean
    public DiscountService discountService() {
        return new DiscountServiceImpl(discountRepository(), discountLevelRepository());
    }

    @Bean
    public DiscountRepository discountRepository() {
        DiscountRepository mock = mock(DiscountRepository.class);
        when(mock.save(any())).thenAnswer(invocationOnMock -> {
            Discount input = (Discount) invocationOnMock.getArguments()[0];
            Random random = new Random();
            long id = random.nextLong();
            return Discount.builder()
                    .id(id)
                    .salesOrg(input.getSalesOrg())
                    .materialNumber(input.getMaterialNumber())
                    .materialDesignation(input.getMaterialDesignation())
                    .standardPrice(input.getStandardPrice())
                    .discountLevels(input.getDiscountLevels())
                    .build();
        });
        return mock;
    }

    @Bean
    public DiscountLevelRepository discountLevelRepository() {
        return mock(DiscountLevelRepository.class);
    }
}
