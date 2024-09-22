package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.BookingType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;


@Data
public class VerifyQrBookingCarReqDTO extends BaseRequest {

    @NotBlank
    private String bookingDetailId;

    @NotBlank
    @EnumRegex(enumClass = BookingType.class)
    private String type;

}
