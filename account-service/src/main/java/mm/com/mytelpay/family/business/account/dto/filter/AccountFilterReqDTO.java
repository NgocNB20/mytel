package mm.com.mytelpay.family.business.account.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;
import mm.com.mytelpay.family.utils.Util;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountFilterReqDTO extends BasePagination {

    private String name;

    private String msisdn;

    private String correctMsisdn;

    private String email;

    private String unitId;

    @EnumRegex(enumClass = Status.class)
    private String status;

    public String getMsisdn() {
        msisdn = Util.refineMobileNumber(msisdn);
        return msisdn;
    }

    public String getCorrectMsisdn() {
        correctMsisdn = Util.refineMobileNumber(correctMsisdn);
        return correctMsisdn;
    }
}
