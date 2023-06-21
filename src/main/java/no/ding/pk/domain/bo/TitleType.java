package no.ding.pk.domain.bo;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "title_types")
@Entity
public class TitleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String titleType;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "titleType")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<KeyCombination> keyCombinations;

    public void addKeyCombination(KeyCombination keyCombination) {
        if(keyCombinations == null) {
            keyCombinations = new ArrayList<>();
        }

        keyCombination.setTitleType(this);

        keyCombinations.add(keyCombination);
    }
}
