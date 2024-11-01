package ru.practicum.ewm.event.repository.compilation;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.model.Compilation;

@Component
public class CompilationSpecification {
    public Specification<Compilation> hasPinnedEqual(Boolean pinned) {
        return  ((root, query, criteriaBuilder) -> pinned == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("pinned"),pinned));
    }
}