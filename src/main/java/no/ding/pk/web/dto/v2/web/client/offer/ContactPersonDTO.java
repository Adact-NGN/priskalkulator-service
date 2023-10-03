package no.ding.pk.web.dto.v2.web.client.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContactPersonDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    private String emailAddress;
}
