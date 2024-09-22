package mm.com.mytelpay.family.business.meal.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
public class MealFilterReqDTO extends BasePagination {

    private String name;
    private String day;
    @EnumRegex(enumClass = MealType.class)
    private String type;
    private String canteenId;
    private String price;
    @EnumRegex(enumClass = Status.class)
    private String status;

}
