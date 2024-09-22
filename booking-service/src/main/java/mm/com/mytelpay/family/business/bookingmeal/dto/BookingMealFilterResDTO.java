package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.MealType;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BookingMealFilterResDTO {
    private MealType mealType;
    private long totalOrder;

}