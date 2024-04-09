package su.weblock.audit.service.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;

public record Order(@NotBlank AuditEventPayload.AuditEventFieldName field, @NotNull Sort.Direction direction) {}
