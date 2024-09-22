package mm.com.mytelpay.family.business.account.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.NumberPhoneRegex;

@Data
public class RefreshTokenReqDTO extends BaseRequest {

    @NotBlank
    @NumberPhoneRegex
    private String msisdn;

    @NotBlank
    private String refreshToken;

}
