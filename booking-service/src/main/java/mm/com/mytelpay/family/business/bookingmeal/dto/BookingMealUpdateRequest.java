package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.BookingStatus;


@Data
public class BookingMealUpdateRequest extends BaseRequest {

    private String id;

    private BookingStatus bookingStatus;

}
