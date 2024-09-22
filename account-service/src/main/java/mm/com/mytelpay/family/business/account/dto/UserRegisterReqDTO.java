package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.OsType;
import mm.com.mytelpay.family.exception.validate.*;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 100)
    private String fullName;

    @NotBlank
    @NumberPhoneRegex
    private String msisdn;

    @NotBlank
    @PinRegex
    private String password;

    @NotBlank
    @PinRegex
    private String confirmPassword;

    @NotBlank
    @EmailRegex
    @SizeRegex(max = 50)
    private String email;

    @NotBlank
    @SizeRegex(max = 100)
    private String unitId;

    @NotBlank
    private String deviceId;

    @NotBlank
    @EnumRegex(enumClass = OsType.class)
    private String os;

    public String getMsisdn(){
        msisdn = Util.refineMobileNumber(msisdn);
        return msisdn;
    }

    public String getEmail(){
        return StringUtils.lowerCase(email);
    }

}
