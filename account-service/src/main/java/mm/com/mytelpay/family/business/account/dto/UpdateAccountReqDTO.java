package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.*;
import mm.com.mytelpay.family.utils.Util;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 100)
    private String id;

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
    @EnumRegex(enumClass = Status.class)
    @SizeRegex(max = 10)
    private String status;

    private List<String> roleIdList;

    private String canteenId;

    public String getPhone(){
        phone = Util.refineMobileNumber(phone);
        return phone;
    }

}
