package no.ding.pk.domain.offer;

import lombok.*;
import no.ding.pk.domain.Auditable;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double customerPrice;

    @Column
    private Double discountLevelPct;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
    @JoinColumn(name = "material_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "Fk_priceRow_material"))
    private Material material;

    @Column
    private String deviceType;

    @Column
    private String devicePlacement;

    @Column
    private Boolean showPriceInOffer;

    @Column
    private Double manualPrice;

    @Column
    private Double discountedPrice;

    @Column
    private Integer discountLevel;

    @Column()
    private Boolean needsApproval = false;

    @Column()
    private Boolean approved = false;
    
    @Column
    private Double discountLevelPrice;

    @Column
    private Double standardPrice;

    @Column
    private Integer amount;

    @Column
    private Double priceIncMva;

    @Column
    private String categoryId; // "
    @Column
    private String categoryDescription; // "
    @Column
    private String subCategoryId; // "
    @Column
    private String subCategoryDescription; // "
    @Column
    private String classId; // "
    @Column
    private String classDescription; // "

    @Column
    private String salesZone;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(foreignKey = @ForeignKey(name = "Fk_priceRow_combinedMaterials"))
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PriceRow> combinedMaterials;

    public boolean hasCombinedMaterials() {
        return combinedMaterials != null && !combinedMaterials.isEmpty();
    }

    public Boolean getNeedsApproval() {
        return needsApproval != null && needsApproval;
    }

    public Boolean isApproved() {
        return approved != null && approved;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "createdBy = " + createdBy + ", " +
                "createdDate = " + createdDate + ", " +
                "lastModifiedBy = " + lastModifiedBy + ", " +
                "lastModifiedDate = " + lastModifiedDate + ", " +
                "customerPrice = " + customerPrice + ", " +
                "discountPct = " + discountLevelPct + ", " +
                "material = " + material + ", " +
                "showPriceInOffer = " + showPriceInOffer + ", " +
                "manualPrice = " + manualPrice + ", " +
                "discountLevel = " + discountLevel + ", " +
                "discountLevelPrice = " + discountLevelPrice + ", " +
                "standardPrice = " + standardPrice + ", " +
                "amount = " + amount + ", " +
                "priceIncMva = " + priceIncMva + ", " +
                "categoryId = " + categoryId + ", " +
                "categoryDescription = " + categoryDescription + ", " +
                "subCategoryId = " + subCategoryId + ", " +
                "subCategoryDescription = " + subCategoryDescription + ", " +
                "classId = " + classId + ", " +
                "classDescription = " + classDescription + ")";
    }
}
