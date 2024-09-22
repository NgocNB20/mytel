package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingMealResponse {

    private String transactionId;

    private LocalDateTime transactionAt;

    private BookingStatus status;

}
