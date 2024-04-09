package su.weblock.audit.service.filter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import su.weblock.common.criteria.ComparisonOperator;
import su.weblock.common.criteria.SearchCriteria;

import java.util.List;
import java.util.Set;

import static su.weblock.audit.model.AuditEventEntity.*;
import static su.weblock.common.criteria.ComparisonOperator.contains;
import static su.weblock.common.criteria.ComparisonOperator.equal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditEventPayload implements SearchCriteria, Sortable {
    @Getter
    public enum AuditEventFieldName {
        eventId(EVENT_ID, equal, contains),
        clusterId(CLUSTER_ID, equal, contains),
        clusterName(CLUSTER_NAME, equal, contains),
        serverIndex(SERVER_INDEX, equal, contains),
        timestamp(TIMESTAMP, equal, contains),
        username(USERNAME, equal, contains),
        eventType(EVENT_TYPE, equal, contains),
        entityType(ENTITY_TYPE, equal, contains),
        serviceName(SERVICE_NAME, equal, contains),
        event(EVENT, equal, contains);

        private final String fieldName;
        private final Set<ComparisonOperator> allowedOperators;

        AuditEventFieldName(String fieldName, ComparisonOperator... allowedOperators) {
            this.fieldName = fieldName;
            this.allowedOperators = Set.of(allowedOperators);
        }
    }

    @NotNull
    private List<List<Filter>> filters;
    @NotNull
    private List<Order> orders;
    @NotNull
    @Valid
    private Page page;
}
