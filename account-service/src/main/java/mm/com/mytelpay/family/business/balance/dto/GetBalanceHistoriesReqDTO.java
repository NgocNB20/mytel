package mm.com.mytelpay.family.business.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mm.com.mytelpay.family.exception.validate.DateFilterRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@AllArgsConstructor
public class GetBalanceHistoriesReqDTO extends BasePagination {

    @DateFilterRegex
    private String from;

    @DateFilterRegex
    private String to;

}
