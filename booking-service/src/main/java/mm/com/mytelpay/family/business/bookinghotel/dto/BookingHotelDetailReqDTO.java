package mm.com.mytelpay.family.business.bookinghotel.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;


@Data
public class BookingHotelDetailReqDTO extends BaseRequest {

    @NotBlank
    private String bookingId;

}
