package su.weblock.common.criteria;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Slf4j
@AllArgsConstructor
public abstract class AbstractSearchSpecification<T, S extends SearchCriteria> implements Specification<T> {
    private static final Set<String> BOOLEAN_VALUES = Set.of("true", "false");

    protected final S criteria;

    @Nullable
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        final List<Predicate> predicates = new ArrayList<>();
        final List<List<SearchCriteria.Filter>> criteriaFilters = criteria.getFilters();

        for (int i = 0; i < criteriaFilters.size(); i++) {
            final List<SearchCriteria.Filter> filters = criteriaFilters.get(i);
            if (!filters.isEmpty()) {
                final Predicate predicate = buildOrPredicate(i, cb, root, filters);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }
        }
        var predicate = buildAndPredicate(cb, predicates);

        return finalize(cb, root, query, predicate);
    }

    protected Predicate finalize(CriteriaBuilder cb, Root<T> root, final CriteriaQuery<?> query, Predicate predicate) {
        return predicate != null ? predicate : cb.conjunction();
    }

    @Nullable
    private Predicate buildAndPredicate(final CriteriaBuilder cb, final List<Predicate> predicates) {
        final Predicate[] predicatesArray = predicates.stream()
                                                      .filter(Objects::nonNull)
                                                      .toArray(Predicate[]::new);

        return predicatesArray.length > 0 ? cb.and(predicatesArray) : null;
    }

    private Predicate buildOrPredicate(
            final int step,
            final CriteriaBuilder cb,
            final Root<T> root,
            final List<SearchCriteria.Filter> filters
    ) {
        final Predicate[] predicates = filters.stream()
                                              .map(filter -> buildPredicate(step, cb, root, filter))
                                              .filter(Objects::nonNull)
                                              .toArray(Predicate[]::new);

        return predicates.length > 0 ? cb.or(predicates) : null;
    }

    public static Predicate buildFromDefaultOperator(CriteriaBuilder cb, Path<?> path, SearchCriteria.Filter filter) {
        final Path<String> field = path.get(filter.field());
        final String value = filter.value();

        return switch (filter.operator()) {
            case equal -> isValueBoolean(value)
                    ? cb.equal(field, Boolean.parseBoolean(value))
                    : cb.equal(field, value);
            case equalIgnoreCase -> cb.equal(cb.lower(field), filter.value().toLowerCase(Locale.ROOT));
            case less -> cb.lessThan(field, filter.value());
            case lessEqual -> cb.lessThanOrEqualTo(field, filter.value());
            case greater -> cb.greaterThan(field, filter.value());
            case greaterEqual -> cb.greaterThanOrEqualTo(field, filter.value());
            case contains -> cb.like(field, "%" + filter.value() + "%");
            case containsIgnoreCase -> cb.like(cb.lower(field), "%" + filter.value().toLowerCase(Locale.ROOT) + "%");
            case notContains -> cb.notLike(field, "%" + filter.value() + "%");
        };
    }

    protected <Q> Subquery<?> createSubquery(CriteriaQuery<?> query,
                                           Root<?> root,
                                           String joinName,
                                           String joinField,
                                           Set<String> collection,
                                           Class<Q> resultClass) {
        Subquery<Q> subquery = query.subquery(resultClass);
        Root<?> subRoot = subquery.correlate(root);
        Join<?, Q> join = subRoot.join(joinName);

        subquery.select(join)
                .where(
                        join.get(joinField).in(collection)
                );
        return subquery;
    }

    protected abstract Predicate buildPredicate(
            final int step,
            final CriteriaBuilder cb,
            final Root<T> root,
            final SearchCriteria.Filter next
    );

    private static boolean isValueBoolean(String value) {
        return BOOLEAN_VALUES.contains(value);
    }
}
