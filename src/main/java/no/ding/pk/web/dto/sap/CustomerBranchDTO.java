package no.ding.pk.web.dto.sap;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBranchDTO {
    @JsonAlias("BranchSystem")
    private String branchSystem;

    @JsonAlias("Branch")
    private String branch;

    @JsonAlias("BranchSystemText")
    private String branchSystemText;

    @JsonAlias("BranchText")
    private String branchText;
}
