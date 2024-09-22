package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.PinRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest extends BaseRequest {

    @NotBlank
    private String currentPass;

    @SizeRegex(max = 6)
    @NotBlank
    @PinRegex
    private String newPass;

    @SizeRegex(max = 6)
    @NotBlank
    @PinRegex
    private String confirmNewPass;

    private String deviceId;

    private String os;

}

