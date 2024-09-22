package mm.com.mytelpay.family.business.car.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
public class CarFilterReqDTO extends BasePagination {

    @EnumRegex(enumClass = CarType.class)
    private String carType;

    @SizeRegex(max = 100)
    private String name;

    @EnumRegex(enumClass = Status.class)
    private String status;
}
