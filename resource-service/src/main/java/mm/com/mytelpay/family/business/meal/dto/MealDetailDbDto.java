package mm.com.mytelpay.family.business.meal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.enums.MealType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealDetailDbDto {

    private String id;
    private String name;
    private Day day;
    private MealType type;
    private LocalDateTime createdAt;
    private Integer price;
    private String canteenId;
    private String canteenName;
    private String foodId;
    private String foodName;
    private FoodType foodType;
    private LocalDateTime foodCreatedAt;
    private Long fileId;
    private String fileName;
    private String url;
}
