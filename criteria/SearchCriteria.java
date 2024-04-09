package su.weblock.common.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public interface SearchCriteria {
    record Filter(@NotBlank String field, @NotNull ComparisonOperator operator, @NotBlank String value) {}
    record Page(@PositiveOrZero Integer number, @Positive Integer size) {}

    List<List<Filter>> getFilters();
}
