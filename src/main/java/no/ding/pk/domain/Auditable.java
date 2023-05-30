package no.ding.pk.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable implements Serializable {
    
    @CreatedBy
    @Column(name = "CREATED_BY", columnDefinition = "varchar(25)", updatable = false)
    protected String createdBy;
 
    @CreatedDate
    @Column(name = "CREATE_DATE", columnDefinition = "timestamp default '2021-06-10 20:47:05.967394'", updatable = false)
    protected Date createdDate;
 
    @LastModifiedBy
    @Column(name = "MODIFIED_BY", columnDefinition = "varchar(25)")
    protected String lastModifiedBy;
 
    @LastModifiedDate
    @Column(name = "MODIFIED_DATE", columnDefinition = "timestamp default '2021-06-10 20:47:05.967394'")
    protected Date lastModifiedDate;

}
