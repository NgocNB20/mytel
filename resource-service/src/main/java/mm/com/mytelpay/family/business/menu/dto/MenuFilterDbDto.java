package mm.com.mytelpay.family.business.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.enums.MealType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuFilterDbDto {
    private Day day;
    private String name;
    private String mealId;
    private MealType type;
    private Integer price;
    private LocalDateTime createdAt;
    private String foodId;
    private String foodName;
    private FoodType foodType;
    private LocalDateTime foodCreatedAt;
    private Long fileId;
    private String fileName;
    private String url;
}
