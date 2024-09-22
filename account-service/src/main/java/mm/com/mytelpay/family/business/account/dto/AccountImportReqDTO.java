package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.*;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountImportReqDTO extends BaseRequest {
    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @NotBlank
    @NumberPhoneRegex
    private String phone;

    @NotBlank
    @EmailRegex
    private String email;

    @NotBlank
    @SizeRegex(max = 100)
    private String unitCode;

    @NotBlank
    @PinRegex
    private String password;

    public String getEmail(){
        return StringUtils.lowerCase(email);
    }

}
