package no.ding.pk.config.listeners;

import org.springframework.stereotype.Component;

@Component
public class PingAuditListener {
    
    // private AuditService auditService;

    // public PingAuditListener(AuditService auditService) {
    //     this.auditService = auditService;
    // }

    // @PrePersist
    // public void beforeCreate(Object object) {
    //     final Audit audit = createAudit(object, "CREATE");
    //     auditService.save(audit);
    // }

    // @PreUpdate
    // private void beforeUpdate(Object object) { 
    //     final Audit audit = createAudit(object, "UPDATE");
    //     auditService.save(audit);
    //  }

    // @PreRemove
    // private void beforeRemove(Object object) { 
    //     final Audit audit = createAudit(object, "REMOVE");
    //     auditService.save(audit);
    //  }

    // private Audit createAudit(Object object, String type) {
    //     final Audit auditDTO = new Audit();

    //     auditDTO.setEntity(object.getClass().getSimpleName());
    //     auditDTO.setOperation(type);
    //     // auditDTO.setModifiedBy(SecurityUtils.getCurrentUserLogin().get());
    //     auditDTO.setModifiedAt(new Date());
    //     auditDTO.setPayload(object.toString());

    //     return auditDTO;
    // }

    
}
