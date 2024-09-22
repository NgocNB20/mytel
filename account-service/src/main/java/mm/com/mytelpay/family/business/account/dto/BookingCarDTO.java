package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCarDTO extends BaseRequest {
    private String id;
    private LocalDateTime timeStart;
    private LocalDateTime timeReturn;
    private CarBookingType typeBooking;
    private Integer quantity;
    private BookingStatus bookingStatus;
    private String accountId;
    private String note;
    private String reason;
    private String original;
    private String destination;
    private String description;
    private Integer approveLevel;
    private String unitId;
}
