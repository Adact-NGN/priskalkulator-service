package no.ding.pk.web.dto.web.client.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.web.dto.web.client.UserDTO;

import java.util.Date;
import java.util.List;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceOfferDTO {
    private Long id;
    private String customerNumber;
    private String customerName;
    private List<SalesOfficeDTO> salesOfficeList;
    private Date dateUpdated;
    private Date dateCreated;
    private UserDTO salesEmployee;
    private Date approvalDate;
    private UserDTO approver;
    private Boolean isApproved;
    private Boolean needsApproval;
    private TermsDTO customerTerms;
    private Date dateIssued;
}
