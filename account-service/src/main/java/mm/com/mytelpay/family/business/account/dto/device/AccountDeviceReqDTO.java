package mm.com.mytelpay.family.business.account.dto.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class AccountDeviceReqDTO extends BaseRequest {

    private String accountId;

    private String roleId;

    @EnumRegex(enumClass = RoleType.class)
    private String roleCode;

}
