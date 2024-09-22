package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.MealType;

@Data
public class PriceMealDTO {
    private MealType mealType;

    private Integer price;
}
