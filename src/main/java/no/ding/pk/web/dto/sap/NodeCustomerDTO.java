package no.ding.pk.web.dto.sap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeCustomerDTO {

    /** 
     * Customer number for <b>Node</b> customers 
     */
    private String hkunnr;

    /**
     * Customer number for customer belonging to a Node customer
     */
    private String kunnr;
}
