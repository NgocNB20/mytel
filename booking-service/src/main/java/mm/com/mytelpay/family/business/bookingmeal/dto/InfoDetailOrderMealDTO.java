package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoDetailOrderMealDTO {

    private String id;

    private MealType meal;

    private MealDetailStatus status;

    private LocalDateTime mealDay;
    private LocalDateTime createdAt;

    public InfoDetailOrderMealDTO(String id, MealDetailStatus status, LocalDateTime mealDay, LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.mealDay = mealDay;
        this.createdAt = createdAt;
    }
}
