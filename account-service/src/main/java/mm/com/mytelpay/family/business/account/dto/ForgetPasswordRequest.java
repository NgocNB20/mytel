package mm.com.mytelpay.family.business.account.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.OsType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.NumberPhoneRegex;
import mm.com.mytelpay.family.utils.Util;


@EqualsAndHashCode(callSuper = true)
@Data
public class ForgetPasswordRequest extends BaseRequest {
    @NotBlank
    @NumberPhoneRegex
    private String msisdn;
    @NotBlank
    private String deviceId;

    @EnumRegex(enumClass = OsType.class)
    @NotBlank
    private String os;

    public String getMsisdn(){
        msisdn = Util.refineMobileNumber(msisdn);
        return msisdn;
    }
}
