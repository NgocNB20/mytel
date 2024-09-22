package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class BookingCarReqDTO extends BaseRequest {

    @EnumRegex(enumClass = CarBookingType.class)
    private String typeBooking;

    @NotBlank
    @SizeRegex(min = 1)
    private Integer quantity;

    @NotBlank
    private LocalDateTime timeStart;

    private LocalDateTime timeReturn;

    @NotBlank
    private String original;

    @NotBlank
    private String destination;

    private String note;

    private Double distance;
}
