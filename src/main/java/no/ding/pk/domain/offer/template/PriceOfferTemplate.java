package no.ding.pk.domain.offer.template;

import lombok.*;
import no.ding.pk.domain.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "price_offer_template")
public class PriceOfferTemplate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String name;

    @Column
    private Boolean isShareable;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pot_author_id", foreignKey = @ForeignKey(name = "Fk_offer_template_author"))
    private User author;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "pot_sharedWithList_id", foreignKey = @ForeignKey(name = "Fk_price_offer_template_sharedWithList"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> sharedWith;

    @OneToMany
    @JoinColumn(name = "pot_materials_id", foreignKey = @ForeignKey(name = "Fk_price_offer_template_materials"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TemplateMaterial> materials;

    @OneToMany
    @JoinColumn(name = "pot_zoneBasedMaterials_id", foreignKey = @ForeignKey(name = "Fk_price_offer_template_zoneBasedMaterials"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TemplateMaterial> zoneBasedMaterials;
}


