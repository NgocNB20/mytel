package mm.com.mytelpay.family.business.menu.dto;

import lombok.Data;
import lombok.Getter;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;

import java.util.List;

@Data
@Getter
public class MealListDto {

    @EnumRegex(enumClass = MealType.class)
    private String mealType;
    private List<String> listFoodId;
}
