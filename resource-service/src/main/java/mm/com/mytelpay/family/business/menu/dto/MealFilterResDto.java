package mm.com.mytelpay.family.business.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.MealType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealFilterResDto implements Comparable<MealFilterResDto> {
    private String id;
    private MealType mealType;
    private Integer price;
    private LocalDateTime createdAt;
    private Set<FoodFilterResDto> foods;

    @Override
    public int compareTo(@NotNull MealFilterResDto o) {
        return this.getMealType().compareTo(o.getMealType());
    }
}
