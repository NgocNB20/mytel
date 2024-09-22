package mm.com.mytelpay.family.business.bookinghotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.exception.validate.DateFilterRegex;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@AllArgsConstructor
public class FilterBookingHotelReqDTO extends BasePagination {

    @EnumRegex(enumClass = BookingStatus.class)
    private String status;

    @DateFilterRegex
    private String from;

    @DateFilterRegex
    private String to;


}
