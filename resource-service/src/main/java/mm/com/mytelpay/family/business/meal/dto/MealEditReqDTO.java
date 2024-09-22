package mm.com.mytelpay.family.business.meal.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;

@Data
public class MealEditReqDTO extends BaseRequest {

    private String id;

    private String name;
    private String canteenId;

    private String day;

    @EnumRegex(enumClass = MealType.class)
    private String type;

    private Double price;

    @EnumRegex(enumClass = Status.class)
    private String status;

}
