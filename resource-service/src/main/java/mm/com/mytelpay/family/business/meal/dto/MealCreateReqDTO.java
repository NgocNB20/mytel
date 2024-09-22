package mm.com.mytelpay.family.business.meal.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;

@Data
public class MealCreateReqDTO extends BaseRequest {

    private String name;


    private String day;
    private String createdBy;

    @EnumRegex(enumClass = MealType.class)
    private String type;

    private Double price;

    @EnumRegex(enumClass = MealType.class)
    private String status;

}
