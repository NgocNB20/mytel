package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.OTPRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.utils.Util;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVerifyOtpReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 12)
    private String msisdn;

    @NotBlank
    @OTPRegex
    private String otp;

    @NotBlank
    private String originalRequestId;

    public String getMsisdn(){
        msisdn = Util.refineMobileNumber(msisdn);
        return msisdn;
    }

}
