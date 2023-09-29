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
    private String customerType;
    private String streetAddress;
    private String postalNumber;
    private String city;
    private String organizationNumber;
    private Date approvalDate;
    private Date dateIssued;
    private List<ContactPersonDTO> contactPerson;
    private String priceOfferStatus;
    private List<SalesOfficeDTO> salesOfficeList;
    private UserDTO salesEmployee;
    private UserDTO approver;
    private Boolean needsApproval;
    private String generalComment;
    private String additionalInformation;
    private Date activationDate;
    private TermsDTO customerTerms;
    private Date dateUpdated;
    private Date dateCreated;
    private String createdBy;
    private String lastModifiedBy;
}
