package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.*;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAccountReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @NotBlank
    @NumberPhoneRegex
    private String phone;

    @NotBlank
    @EmailRegex
    @SizeRegex(max = 50)
    private String email;

    @NotBlank
    @SizeRegex(max = 100)
    private String unitId;

    @NotBlank
    @PinRegex
    private String password;

    @SizeRegex(max = 100)
    private List<String> roleIdList;

    private String canteenId;

    public String getPhone(){
        phone = Util.refineMobileNumber(phone);
        return phone;
    }

    public String getEmail(){
        return StringUtils.lowerCase(email);
    }

}