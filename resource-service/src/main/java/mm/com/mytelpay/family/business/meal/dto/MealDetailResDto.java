package mm.com.mytelpay.family.business.meal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.enums.MealType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealDetailResDto {
    private String id;
    private String name;
    private String canteenName;
    private Day day;
    private LocalDateTime createdAt;
    private Integer price;
    private MealType type;
    private Set<FoodDetailDto> foods;
}
