package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;

import java.util.List;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class FilterBookingMealResDTO {

    private String mealDay;

    private List<FilterBookingMealDetailDTO> listOrderMeal;
}
