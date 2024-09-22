package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.MealType;

@Data
public class MealResDTO {
    private String id;

    private MealType mealType;
}
