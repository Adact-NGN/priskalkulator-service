package no.ding.pk.repository.specifications;

import no.ding.pk.domain.offer.PriceOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public class PriceOfferStreamSpec implements StreamableJpaSpecification<PriceOffer> {
    @Autowired
    private EntityManager entityManager;


    @Override
    public Stream<PriceOffer> stream(Specification<PriceOffer> specification, Class<PriceOffer> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PriceOffer> cq = cb.createQuery(clazz);
        Root<PriceOffer> root = cq.from(clazz);

        cq.select(root);

        if(specification != null) {
            cq.where(specification.toPredicate(root, cq, cb));
        }

        return entityManager.createQuery(cq)
                .setHint(HINT_FETCH_SIZE, "1")
                .getResultStream();
    }
}
