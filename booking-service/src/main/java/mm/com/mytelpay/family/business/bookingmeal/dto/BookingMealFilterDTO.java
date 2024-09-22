package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingMealFilterDTO {
    private List<BookingMealFilterResDTO> bookingMealFilterResDTOS;
    private Integer totalOrder;
}
