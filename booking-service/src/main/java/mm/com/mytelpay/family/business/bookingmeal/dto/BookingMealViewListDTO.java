package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.MealDetailStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingMealViewListDTO  {

    private String bookingMealId;
    private String accountId;
    private String userName;
    private String userPhone;
    private String avtUrl;
    private MealDetailStatus status;
    private LocalDateTime createdAt;

    public BookingMealViewListDTO(String bookingMealId, MealDetailStatus status, LocalDateTime createdAt, String accountId) {
        this.bookingMealId = bookingMealId;
        this.status = status;
        this.createdAt = createdAt;
        this.accountId = accountId;
    }
}
