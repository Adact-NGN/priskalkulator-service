package no.ding.pk.web.dto.v2.web.client.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.ding.pk.web.dto.v2.web.client.offer.TermsDTO;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ActivatePriceOfferRequest {
    private TermsDTO terms;
    private String generalComment;
}
