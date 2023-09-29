package no.ding.pk.config.mapping.mapstruct.v2;

import no.ding.pk.domain.offer.ContactPerson;
import no.ding.pk.web.dto.web.client.offer.ContactPersonDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ContactPersonMapper {
    ContactPerson contactPersonDtoToContactPerson(ContactPersonDTO contactPersonDTO);

    ContactPersonDTO contactPersonToContactPersonDto(ContactPerson contactPerson);
}
