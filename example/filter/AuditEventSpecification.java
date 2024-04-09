package su.weblock.audit.service.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import su.weblock.audit.model.AuditEventEntity;
import su.weblock.common.audit.AuditEntityType;
import su.weblock.common.audit.AuditEventType;
import su.weblock.common.audit.ServiceName;
import su.weblock.common.criteria.AbstractSearchSpecification;
import su.weblock.common.criteria.ComparisonOperator;
import su.weblock.common.criteria.SearchCriteria;

import static su.weblock.audit.model.AuditEventEntity.ENTITY_TYPE;
import static su.weblock.audit.model.AuditEventEntity.EVENT_TYPE;
import static su.weblock.audit.model.AuditEventEntity.FIELDS;
import static su.weblock.audit.model.AuditEventEntity.SERVICE_NAME;

public class AuditEventSpecification extends AbstractSearchSpecification<AuditEventEntity, AuditEventPayload> {
    public AuditEventSpecification(final AuditEventPayload criteria) {
        super(criteria);
    }

    @Override
    protected Predicate buildPredicate(final int step,
                                       final CriteriaBuilder cb,
                                       final Root<AuditEventEntity> root,
                                       final SearchCriteria.Filter filter) {

        final String fieldName = filter.field();

        if (isServiceNameValue(filter)) {
            return cb.equal(root.get(fieldName), ServiceName.valueOf(filter.value()));
        }
        if (isEventTypeValue(filter)) {
            return cb.equal(root.get(fieldName), AuditEventType.valueOf(filter.value()));
        }
        if (isEntityTypeValue(filter)) {
            return cb.equal(root.get(fieldName), AuditEntityType.valueOf(filter.value()));
        }
        if (FIELDS.contains(fieldName)) {
            return buildFromDefaultOperator(cb, root, filter);
        }
        return null;
    }

    private boolean isServiceNameValue(final SearchCriteria.Filter filter) {
        return filter.field().equals(SERVICE_NAME) && isOperatorEqualOrContains(filter.operator());
    }

    private boolean isEventTypeValue(final SearchCriteria.Filter filter) {
        return filter.field().equals(EVENT_TYPE) && isOperatorEqualOrContains(filter.operator());
    }

    private boolean isEntityTypeValue(final SearchCriteria.Filter filter) {
        return filter.field().equals(ENTITY_TYPE) && isOperatorEqualOrContains(filter.operator());
    }

    private boolean isOperatorEqualOrContains(final ComparisonOperator operator) {
        return operator.equals(ComparisonOperator.equal)
                || operator.equals(ComparisonOperator.contains);
    }
}
