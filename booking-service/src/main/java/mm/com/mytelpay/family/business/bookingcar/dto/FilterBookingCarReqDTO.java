package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mm.com.mytelpay.family.enums.AssignStatus;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.exception.validate.DateFilterRegex;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@AllArgsConstructor
public class FilterBookingCarReqDTO extends BasePagination {

    @EnumRegex(enumClass = CarBookingType.class)
    private String carTypeBooking;

    @EnumRegex(enumClass = AssignStatus.class)
    private String assignStatus;

    @EnumRegex(enumClass = BookingStatus.class)
    private String status;

    @DateFilterRegex (message = "parameter.invalid.from")
    private String from;

    @DateFilterRegex (message = "parameter.invalid.to")
    private String to;
}
