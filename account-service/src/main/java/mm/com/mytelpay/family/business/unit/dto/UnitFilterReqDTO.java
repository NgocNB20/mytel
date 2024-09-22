package mm.com.mytelpay.family.business.unit.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
public class UnitFilterReqDTO extends BasePagination {

    @SizeRegex(max = 100)
    private String name;
    @SizeRegex(max = 50)
    private String code;

}
