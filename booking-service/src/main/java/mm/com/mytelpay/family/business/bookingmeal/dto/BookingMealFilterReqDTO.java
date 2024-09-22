package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingMealFilterReqDTO extends BaseRequest {
    private String from;
    private String to;
    @EnumRegex(enumClass = BookingStatus.class)
    private String status;
}
