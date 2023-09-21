package no.ding.pk.domain.bo;

import lombok.*;

import javax.persistence.*;

/**
 * Key Combination => Nøkkelkombinasjon
 */
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

    /**
     * Key Combination table name
     */
    @Column
    private String keyCombination;

    /**
     * Key Combination description
     */
    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "condition_code_id", foreignKey = @ForeignKey(name = "FK_key_combination_condition_code"))
    private ConditionCode conditionCode;
}
