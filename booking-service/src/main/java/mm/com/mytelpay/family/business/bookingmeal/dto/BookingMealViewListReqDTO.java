package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;
import mm.com.mytelpay.family.utils.Util;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingMealViewListReqDTO extends BasePagination {

    private String from;
    private String to;
    @EnumRegex(enumClass = BookingStatus.class)
    private String status;
    private String phoneNumber;

    @EnumRegex(enumClass = MealType.class)
    private String mealType;

    public String getPhoneNumber() {
        phoneNumber = Util.refineMobileNumber(phoneNumber);
        return phoneNumber;
    }
}
