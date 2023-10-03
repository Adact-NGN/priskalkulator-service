package no.ding.pk.web.dto.v2.web.client.offer;

import lombok.Data;

import java.util.Date;

@Data
public class PriceOfferListDTO {
    private Long id;
    private Date dateCreated;
    private Date dateUpdated;
    private String customerNumber;
    private String customerName;
    private SimpleSalesEmployeeDTO salesEmployee; // full name
    private String priceOfferStatus;
}
