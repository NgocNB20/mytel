package mm.com.mytelpay.family.business.account.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.RoleType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountRoleResDto {

    private RoleType code;

    private String name;

    private String roleId;

}
