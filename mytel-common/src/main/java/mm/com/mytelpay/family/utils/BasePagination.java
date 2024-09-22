package mm.com.mytelpay.family.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.Order;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NumberRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import org.apache.commons.lang3.StringUtils;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasePagination extends BaseRequest {

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    @EnumRegex(enumClass = Order.class)
    private String sortOrder = "DESC";

    @SizeRegex(min = 1, max = 100)
    @NumberRegex
    private String pageSize;

    @SizeRegex(min = 1)
    @NumberRegex
    private String pageIndex;

    public void setSortBy(String sortBy) {
        if (StringUtils.isBlank(sortBy)) {
            return;
        }
        this.sortBy = sortBy;
    }

    public void setSortOrder(String sortOrder) {
        if (StringUtils.isBlank(sortOrder)) {
            return;
        }
        this.sortOrder = sortOrder;
    }

    public void setPageSize(String pageSize) {
        if (pageSize == null) {
            return;
        }
        this.pageSize = pageSize;
    }

    public void setPageIndex(String pageIndex) {
        if (pageIndex == null) {
            return;
        }
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        if (StringUtils.isNotBlank(pageSize)) {
            return Integer.parseInt(pageSize);
        } else {
            return null;
        }
    }

    public Integer getPageIndex(){
        if (StringUtils.isNotBlank(pageIndex)) {
            return Integer.parseInt(pageIndex);
        } else {
            return null;
        }
    }
}
