package no.ding.pk.domain.bo;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Condition Code => Betingelsestype
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "condition_codes")
@Entity
public class ConditionCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "conditionCode")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<KeyCombination> keyCombinations;

    public void addKeyCombination(KeyCombination keyCombination) {
        if(keyCombinations == null) {
            keyCombinations = new ArrayList<>();
        }

        keyCombination.setConditionCode(this);

        keyCombinations.add(keyCombination);
    }
}
