package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.validate.DateFilterRegex;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@AllArgsConstructor
public class ReportFeeBookingMealReqDTO extends BasePagination {

    private String phone;

    private String canteenId;

    @EnumRegex(enumClass = MealType.class)
    private String meal;

    private String unitId;

    @DateFilterRegex
    private String fromDate;

    @DateFilterRegex
    private String toDate;


}
