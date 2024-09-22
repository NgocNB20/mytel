package mm.com.mytelpay.family.business.bookinghotel.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.time.LocalDateTime;


@Data
public class BookingHotelCreateReqDTO extends BaseRequest {

    @NotBlank
    private String hotelId;

    @NotBlank
    private LocalDateTime from;

    @NotBlank
    private LocalDateTime to;

    @NotBlank
    @SizeRegex(min = 1)
    private Integer member;

    private String note;

}