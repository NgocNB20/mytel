package mm.com.mytelpay.family.business.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.NumberPhoneRegex;
import mm.com.mytelpay.family.exception.validate.PositiveNumberRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.utils.Util;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopupReqDTO extends BaseRequest {

    @NotBlank
    @NumberPhoneRegex
    private String phone;

    @NotBlank
    @PositiveNumberRegex
    private String amount;

    public String getPhone() {
        phone = Util.refineMobileNumber(phone);
        return phone;
    }

}
