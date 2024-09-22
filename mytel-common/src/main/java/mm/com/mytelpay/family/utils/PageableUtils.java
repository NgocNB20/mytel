package mm.com.mytelpay.family.utils;

import mm.com.mytelpay.family.enums.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import static mm.com.mytelpay.family.utils.Constants.MAX_LIMIT;

public class PageableUtils extends PageRequest {
    public PageableUtils(int page, int size, String orderBy, boolean desc) {
        super(page - 1, size, desc ? Sort.by(orderBy).descending() : Sort.by(orderBy));
    }

    public static Pageable pageable(Integer page, Integer size, String orderBy, String order) {
        if (page == null || size == null) {
            Sort sort = Order.DESC.equals(Order.valueOf(order)) ? Sort.by(orderBy).descending() : Sort.by(orderBy).ascending();
            return PageRequest.of(0, MAX_LIMIT, sort);
        } else {
            Order sortOrder = ObjectUtils.isEmpty(order) ? Order.DESC : Order.valueOf(order);
            return new PageableUtils(page, size, orderBy, Order.DESC.equals(sortOrder));
        }
    }

    public static Pageable pageable(BasePagination page) {
        Order sortOrder = ObjectUtils.isEmpty(page.getSortOrder()) ? Order.DESC : Order.valueOf(page.getSortBy());
        return new PageableUtils(page.getPageIndex(), page.getPageSize(), page.getSortBy(), Order.DESC.equals(sortOrder));
    }
}