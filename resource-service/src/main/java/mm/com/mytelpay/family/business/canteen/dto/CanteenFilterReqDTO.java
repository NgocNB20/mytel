package mm.com.mytelpay.family.business.canteen.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
public class CanteenFilterReqDTO extends BasePagination {

    @SizeRegex(max = 100)
    private String name;
    private String unitId;
    @SizeRegex(max = 50)
    private String code;
}
