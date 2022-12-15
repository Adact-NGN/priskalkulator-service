package no.ding.pk.domain.offer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "price_rows")
public class PriceRow extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Double customerPrice;

    @Column
    private Double discountPct;

    @ManyToOne()
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private Material material;

    @Column
    private Boolean showPriceInOffer;

    @Column
    private Double manualPrice;

    @Column
    private Integer priceLevel;
    
    @Column
    private Double priceLevelPrice;

    @Column
    private Double standardPrice;

    @Column
    private Integer amount;

    @Column
    private Double priceIncMva;

}
