package no.ding.pk.service.sap;

import no.ding.pk.web.dto.sap.CustomerDTO;

import java.util.List;


public interface CustomerService {
    /**
    * Fetches list of customers filtered by parent company and customer type. The service will return up to 100 objects per request. Use skip token to get the next 100 objects.
    * @param parentCompany Filter on parent company.
    * @param customerType Filter on customer type, default 'Node'.
    * @param expansionFields List of fields to get expanded information about.
    * @param skipToken Amount of objects to skip ahead, if not defined only the first 100 results will be returned. For the next 100 you need to skip the first 100 and so on.
    * @return List of CustomerDTO objects.
    */
    List<CustomerDTO> fetchCustomersJSON(String parentCompany, String customerType, List<String> expansionFields, Integer skipToken);

    /**
     * Get list of all customers for a given sales organization.
     * @param salesOrg Sales organisation number.
     * @return List of CustomerDTO objects.
     */
    List<CustomerDTO> findCustomersBySalesOrg(String salesOrg);

    /**
     * Get customers by name and sales organisation.
     * @param salesOrg Sales organisation number.
     * @param name Customer name.
     * @return List of CustomerDTO objects, else empty list.
     */
    List<CustomerDTO> findCustomersBySalesOrgAndName(String salesOrg, String name);

    /**
     * Finds a customer by its customer number.
     * @param knr Customer number
     * @return A list with a single customer object, else empty list.
     */
    List<CustomerDTO> findCustomerByCustomerNumber(String knr);

    /**
     * 
     * @param salesOrg Sales organisation number.
     * @param searchField The object field to search on
     * @param searchString The search string to use in the search.
     * @return List of CustomerDTO objects, else empty list.
     */
    List<CustomerDTO> searchCustomerBy(String salesOrg, String searchField, String searchString);
}
