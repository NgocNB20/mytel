package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FilterBookingMealDetailDTO {

    private String id;

    private String bookingMealId;

    private String mealId;

    private MealType mealType;

    private LocalDate mealDay;

    private MealDetailStatus status ;

    private String canteenId;

    private String canteenName;

    private String canteenAddress;

    private LocalDateTime createdAt;

    public FilterBookingMealDetailDTO(String id, String bookingMealId, String mealId, MealType mealType, LocalDate mealDay,
      MealDetailStatus status, String canteenId, LocalDateTime createdAt) {
        this.id = id;
        this.bookingMealId = bookingMealId;
        this.mealId = mealId;
        this.mealType = mealType;
        this.mealDay = mealDay;
        this.status = status;
        this.canteenId = canteenId;
        this.createdAt = createdAt;
    }
}
