package no.ding.pk.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
//@EqualsAndHashCode
@NamedEntityGraph(name = "SalesRole.userList", attributeNodes = @NamedAttributeNode("userList"))
@Table(name = "sales_roles")
public class SalesRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String roleName; // sellerType
    
    @Column
    private String description;
    
    @Column
//    @EqualsAndHashCode.Exclude
    private Integer defaultPowerOfAttorneyOa;
    
    @Column
//    @EqualsAndHashCode.Exclude
    private Integer defaultPowerOfAttorneyFa;

    @JsonManagedReference
    @Column
    @OneToMany(mappedBy = "salesRole", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @LazyCollection(LazyCollectionOption.FALSE)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SalesRole salesRole = (SalesRole) o;

        return new EqualsBuilder().append(roleName, salesRole.roleName).append(description, salesRole.description).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(roleName).append(description).toHashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "roleName = " + roleName + ", " +
                "description = " + description + ", " +
                "defaultPowerOfAttorneyOa = " + defaultPowerOfAttorneyOa + ", " +
                "defaultPowerOfAttorneyFa = " + defaultPowerOfAttorneyFa + ", " +
                "userList.size() = " + userList.size() + ")";
    }
}