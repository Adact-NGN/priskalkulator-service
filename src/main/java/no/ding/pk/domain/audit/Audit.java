package no.ding.pk.domain.audit;

import java.util.Date;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit")
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "entity", nullable = false)
    private String entity;

    @Column(name = "operation", nullable = false)
    private String operation;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @Column(name = "modified_at", nullable = false)
    private Date modifiedAt;

    @Column(name = "payload", nullable = false)
    private String payload;
}