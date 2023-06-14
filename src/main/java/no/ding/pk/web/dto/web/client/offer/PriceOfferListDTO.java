package no.ding.pk.web.dto.web.client.offer;

import lombok.Data;

import java.util.Date;

@Data
public class PriceOfferListDTO {
    private Long id;
    private Date dateCreated;
    private String customer;
    private String customerName;
    private String salesEmployee;
}
