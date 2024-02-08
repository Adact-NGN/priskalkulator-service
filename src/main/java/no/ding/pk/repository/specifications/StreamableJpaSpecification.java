package no.ding.pk.repository.specifications;

import org.springframework.data.jpa.domain.Specification;

import java.util.stream.Stream;

public interface StreamableJpaSpecification<T> {
    Stream<T> stream(Specification<T> specification, Class<T> clazz);
}
