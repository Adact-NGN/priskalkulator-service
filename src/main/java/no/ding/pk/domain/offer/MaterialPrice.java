package no.ding.pk.domain.offer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.Auditable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// @EntityListeners(PingAuditListener.class)
@Entity
@Table(name = "material_price")
public class MaterialPrice extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String materialNumber;

    @Column
    private Double standardPrice;
}
