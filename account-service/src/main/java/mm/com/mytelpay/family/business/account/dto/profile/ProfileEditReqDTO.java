package mm.com.mytelpay.family.business.account.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.EmailRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEditReqDTO extends BaseRequest {

    @SizeRegex(max = 100)
    private String name;

    @SizeRegex(max = 50)
    @EmailRegex
    private String email;

    private String driverLicense;

}
