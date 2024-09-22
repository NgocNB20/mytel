package mm.com.mytelpay.family.business.bookinghotel.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.UpdateBookingType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
public class ApproveRejectBookingHotelReqDTO extends BaseRequest {

    @NotBlank
    private String bookingId;

    @SizeRegex(max = 255)
    private String reason;

    @NotBlank
    @EnumRegex(enumClass = UpdateBookingType.class)
    private String type;

    @SizeRegex
    private Integer feeBooking;

    @SizeRegex
    private Integer feeService;

}
