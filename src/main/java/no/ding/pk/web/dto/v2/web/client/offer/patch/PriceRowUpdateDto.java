package no.ding.pk.web.dto.v2.web.client.offer.patch;

import org.openapitools.jackson.nullable.JsonNullable;

public class PriceRowUpdateDto {
    private JsonNullable<Long> id;
    private JsonNullable<Double> customerPrice;
    private JsonNullable<Double> discountLevelPct;
    private JsonNullable<Double> discountLevelPrice;
    private JsonNullable<String> material;
    private JsonNullable<String> designation;
    private JsonNullable<String> materialDesignation;
    private JsonNullable<String> productGroupDesignation;
    private JsonNullable<String> deviceType;
    private JsonNullable<String> devicePlacement;
    private JsonNullable<Boolean> showPriceInOffer;
    private JsonNullable<Double> manualPrice;
    private JsonNullable<Double> discountedPrice;
    private JsonNullable<Integer> priceLevel;
    private JsonNullable<Double> priceLevelPrice;
    private JsonNullable<Double> standardPrice;
    private JsonNullable<Integer> pricingUnit;
}
