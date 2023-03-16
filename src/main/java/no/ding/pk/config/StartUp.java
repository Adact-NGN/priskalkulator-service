package no.ding.pk.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ding.pk.domain.Discount;
import no.ding.pk.service.DiscountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StartUp {
    private static final Logger log = LoggerFactory.getLogger(StartUp.class);

    private final DiscountService discountService;

    private final ObjectMapper objectMapper;

    @Autowired
    public StartUp(DiscountService discountService, ObjectMapper objectMapper) {
        this.discountService = discountService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void postConstruct() {

        initializeDiscounts();
    }

    private void initializeDiscounts() {
        log.debug("Initializing Discount matrix");
        TypeReference<List<Discount>> typeReference = new TypeReference<>() {
        };

        List<String> existingDiscounts = discountService.findAll().stream().map(Discount::getMaterialNumber).collect(Collectors.toList());

        InputStream inputStream = TypeReference.class.getResourceAsStream("/discounts.json");

        try {
            List<Discount> discounts = objectMapper.readValue(inputStream, typeReference);

            List<Discount> toPersist = discounts.stream().filter(discount -> !existingDiscounts.contains(discount.getMaterialNumber())).collect(Collectors.toList());
            log.debug("Discounts to persist: {}", toPersist.size());

            discountService.saveAll(toPersist);
            log.debug("Discounts saved");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
