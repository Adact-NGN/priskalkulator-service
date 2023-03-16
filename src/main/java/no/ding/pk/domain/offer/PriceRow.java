package no.ding.pk.domain.offer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.Auditable;

import java.util.List;

@Setter
@Getter
@ToString
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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "material_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "Fk_priceRow_material"))
    private Material material;

    @Column
    private Boolean showPriceInOffer;

    @Column
    private Double manualPrice;

    @Column
    private Integer discountLevel;
    
    @Column
    private Double discountLevelPrice;

    @Column
    private Double standardPrice;

    @Column
    private Integer amount;

    @Column
    private Double priceIncMva;

    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "Fk_priceRow_combinedMaterials"))
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> combinedMaterials;

    public boolean hasCombinedMaterials() {
        return combinedMaterials != null && !combinedMaterials.isEmpty();
    }
}
