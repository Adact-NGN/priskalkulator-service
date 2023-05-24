package no.ding.pk.web.dto.web.client.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApprovalRequest {
    private Boolean status;
    private String comment;
}
