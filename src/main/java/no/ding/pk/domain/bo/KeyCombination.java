package no.ding.pk.domain.bo;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "key_combinations")
@Entity
public class KeyCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String keyCombination;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "title_type_id", foreignKey = @ForeignKey(name = "FK_key_combination_title_type"))
    private TitleType titleType;
}
