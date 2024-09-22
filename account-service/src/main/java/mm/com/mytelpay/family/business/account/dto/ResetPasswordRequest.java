package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NumberPhoneRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.PinRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.utils.Util;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest extends BaseRequest {

    @NotBlank
    @NumberPhoneRegex
    @SizeRegex(max = 12)
    private String msisdn;

    @NotBlank
    @PinRegex
    private String newPass;

    @NotBlank
    @PinRegex
    private String confirmPass;

    @NotBlank
    private String originalRequestId;

    public String getMsisdn(){
        msisdn = Util.refineMobileNumber(msisdn);
        return msisdn;
    }
}

