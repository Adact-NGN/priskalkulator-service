package no.ding.pk.web.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class SalesRoleDTO {
    private Long id;
    private String roleName; // sellerType
    private String description;
    private Integer defaultPowerOfAttorneyOa;
    private Integer defaultPowerOfAttorneyFa;
    @JsonManagedReference
    private List<UserDTO> userList;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getDefaultPowerOfAttorneyOa() {
        return defaultPowerOfAttorneyOa;
    }
    public void setDefaultPowerOfAttorneyOa(Integer defaultPowerOfAttorneyOa) {
        this.defaultPowerOfAttorneyOa = defaultPowerOfAttorneyOa;
    }
    public Integer getDefaultPowerOfAttorneyFa() {
        return defaultPowerOfAttorneyFa;
    }
    public void setDefaultPowerOfAttorneyFa(Integer defaultPowerOfAttorneyFa) {
        this.defaultPowerOfAttorneyFa = defaultPowerOfAttorneyFa;
    }
    public List<UserDTO> getUserList() {
        return userList;
    }
    public void setUserList(List<UserDTO> userList) {
        this.userList = userList;
    }
}
