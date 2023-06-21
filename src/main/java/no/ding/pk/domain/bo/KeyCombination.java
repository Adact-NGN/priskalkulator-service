package no.ding.pk.domain.bo;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "key_combination")
@Entity
public class KeyCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String keyCombination;

    @Column
    private String keyCombinationDescription;
}
