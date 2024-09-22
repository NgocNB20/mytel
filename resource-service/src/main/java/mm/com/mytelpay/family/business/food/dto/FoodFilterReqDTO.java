package mm.com.mytelpay.family.business.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.exception.validate.DateFilterRegex;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodFilterReqDTO extends BasePagination {

    @SizeRegex(max = 100)
    private String name;

    @EnumRegex(enumClass = FoodType.class)
    private String type;

    @DateFilterRegex
    private String from;

    @DateFilterRegex
    private String to;
}
