package no.ding.pk.domain.offer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.ding.pk.domain.Auditable;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
