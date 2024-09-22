package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.enums.MealType;

import java.util.List;

@Data
public class GetMealByIdDTO {

    private String name;

    private String canteenName;

    private Day day;

    private Integer price;

    private MealType type;

    private List<FoodDetailDTO> foods;
}
