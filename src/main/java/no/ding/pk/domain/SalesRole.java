package no.ding.pk.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@NamedEntityGraph(name = "SalesRole.userList", attributeNodes = @NamedAttributeNode("userList"))
@Table(name = "sales_roles")
public class SalesRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique = true)
    private String roleName; // sellerType
    
    @Column
    private String description;
    
    @Column
    private Integer defaultPowerOfAttorneyOa;
    
    @Column
    private Integer defaultPowerOfAttorneyFa;
    
    @JsonManagedReference
    @Column
    @OneToMany(mappedBy = "salesRole", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @Builder.Default private List<User> userList = new ArrayList<>();
    
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
    
    public void addUser(User user) {

        if(userList.contains(user)) {
            return;
        }

        user.setSalesRole(this);
        
        if(userList == null) {
            userList = new ArrayList<>();
        }
        
        userList.add(user);
    }
    
    public void removeUser(User user) {
        if(userList != null) {
            userList.remove(user);
            
            user.setSalesRole(null);
        }
    }
    
    @Override
    public String toString() {
        return "SalesRole [description=" + description + ", id=" + id + ", roleName=" + roleName + ", defaultPowerOfAttorneyOa="+ defaultPowerOfAttorneyOa + ", defaultPowerOfAttorneyFa="+ defaultPowerOfAttorneyFa + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((defaultPowerOfAttorneyOa == null) ? 0 : defaultPowerOfAttorneyOa.hashCode());
        result = prime * result + ((defaultPowerOfAttorneyFa == null) ? 0 : defaultPowerOfAttorneyFa.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SalesRole other = (SalesRole) obj;
        if (roleName == null) {
            if (other.roleName != null)
                return false;
        } else if (!roleName.equals(other.roleName))
            return false;
        if(description == null) {
            if(other.description != null)    
                return false;
        } else if(!description.equals(other.description))
        return false;
        if (defaultPowerOfAttorneyOa == null) {
            if (other.defaultPowerOfAttorneyOa != null)
                return false;
        } else if(!defaultPowerOfAttorneyOa.equals(other.defaultPowerOfAttorneyOa)) {
            return false;
        }
        if (defaultPowerOfAttorneyFa == null) {
            if (other.defaultPowerOfAttorneyFa != null)
                return false;
        } else if(!defaultPowerOfAttorneyFa.equals(other.defaultPowerOfAttorneyFa)) {
            return false;
        }
        
        return true;
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
    
    public List<User> getUserList() {
        if(userList == null) {
            userList = new ArrayList<>();
        }
        return userList;
    }
    
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}