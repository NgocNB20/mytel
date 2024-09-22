package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.DateFilterRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
public class FilterBookingMealEUReqDTO extends BasePagination {

    @DateFilterRegex
    private String from;

    @DateFilterRegex
    private String to;

}
