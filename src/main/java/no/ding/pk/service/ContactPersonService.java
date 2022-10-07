package no.ding.pk.service;

import java.util.List;

import no.ding.pk.web.dto.ContactPersonDTO;

public interface ContactPersonService {
    /**
     * Fetches a list of contact persons. Each fetch only gets 100 items. To get the next 100 tiems set a value for skipTokens.
     * @param expansionFields List of fields to get expanded information about.
     * @param skipTokens Amount of items to skip. 
     * @return List of ContactPersonsDTO objects.
     */
    List<ContactPersonDTO> fetchContactPersons(List<String> expansionFields, Integer skipTokens);

    /**
     * Find contact person by its number.
     * @param contactPersonNumber Contact person number
     * @return A contact person object, else null.
     */
    List<ContactPersonDTO> findContactPersonByNumber(String contactPersonNumber);
}
