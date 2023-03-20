package no.ding.pk.web.controllers;

import no.ding.pk.service.sap.ContactPersonService;
import no.ding.pk.web.dto.sap.ContactPersonDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/contact-person")
public class ContactPersonController {

    private static final Logger log = LoggerFactory.getLogger(ContactPersonController.class);

    private final ContactPersonService service;

    @Autowired
    public ContactPersonController(ContactPersonService contactPersonService) {
        this.service = contactPersonService;
    }

    /**
     * Get list of all contact persons. The service returns 100 entries per request.
     * @param expand Comma separated list of fields to expand, defailt '_Customers'
     * @param skipToken Amount of items to skip for this request.
     * @return List of contact person objects, else empty list.
     */
    @GetMapping("/list")
    public List<ContactPersonDTO> getContactPersons(
        @RequestParam(value = "expand", required = false) String expand,
        @RequestParam(value = "skipToken", required = false) Integer skipToken) {

            log.debug(String.format("Request received with with params: %s", "expand=" + expand + ", skipToken=" + skipToken));

            List<String> expansionFields = new ArrayList<>();
            if(expand != null) {
                expansionFields.addAll(Arrays.asList(expand.split(",")));
            }
        return service.fetchContactPersons(expansionFields, skipToken);
    }

    /**
     * Get contact person object by number.
     * @param contactPersonNumber Contact person number.
     * @return List with one contact person objects, else empty list.
     */
    @GetMapping("/{cpn}")
    public List<ContactPersonDTO> findContactPersonByNumber(@PathVariable("cpn") String contactPersonNumber) {

        log.debug(String.format("Request received with with params: %s", "cpn=" + contactPersonNumber));

        return service.findContactPersonByNumber(contactPersonNumber);
    }
}
