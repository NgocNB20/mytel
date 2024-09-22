package mm.com.mytelpay.family.business.menu.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.MealType;

@Data
public class PriceMealDto {
    private MealType mealType;
    private Integer price;
}
