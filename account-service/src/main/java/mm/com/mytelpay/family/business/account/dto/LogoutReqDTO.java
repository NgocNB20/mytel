package mm.com.mytelpay.family.business.account.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;


@EqualsAndHashCode(callSuper = true)
@Data
public class LogoutReqDTO extends BaseRequest {

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String accountId;

    @NotBlank
    private String deviceId;

}
