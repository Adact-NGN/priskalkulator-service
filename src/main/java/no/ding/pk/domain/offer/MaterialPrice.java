package no.ding.pk.domain.offer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ding.pk.domain.Auditable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
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

    public void copy(MaterialPrice materialStandardPrice) {
        this.standardPrice = materialStandardPrice.getStandardPrice();
    }
}
