package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;
import mm.com.mytelpay.family.utils.Util;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountReportReqDTO extends BasePagination {
    private String phone;
    @EnumRegex(enumClass = BookingStatus.class)
    private String status;
    private String driverId;
    private String bookingType;
    private String fromDate;
    private String toDate;

    public String getPhone(){
        phone = Util.refineMobileNumber(phone);
        return phone;
    }
}
