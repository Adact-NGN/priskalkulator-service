package no.ding.pk.domain.offer.template;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TemplateMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String material;

    @Column
    private String deviceType;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
