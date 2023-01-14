package no.ding.pk.web.dto.web.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Terms;

import java.util.Date;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceOfferDTO {
    private Long id;
    private String customerNumber;
    private String customerName;
    private List<SalesOffice> salesOfficeList;
    private Date dateUpdated;
    private Date dateCreated;
    private User salesEmployee;
    private Date approvalDate;
    private User approver;
    private Boolean isApproved;
    private Boolean needsApproval;
    private Terms customerTerms;
    private Date dateIssued;

}
