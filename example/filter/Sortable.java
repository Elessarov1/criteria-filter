package su.weblock.audit.service.filter;

import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.List;

public interface Sortable {
    List<Order> getOrders();

    default Sort buildSort() {
        Assert.notNull(getOrders(), "The orders field must not be null");
        if (getOrders().isEmpty()) {
            return Sort.unsorted();
        }

        var iterator = getOrders().iterator();
        var order = iterator.next();
        Sort sort = Sort.by(order.direction(), order.field().getFieldName());

        while (iterator.hasNext()) {
            order = iterator.next();
            sort = sort.and(Sort.by(order.direction(), order.field().getFieldName()));
        }
        return sort;
    }
}
