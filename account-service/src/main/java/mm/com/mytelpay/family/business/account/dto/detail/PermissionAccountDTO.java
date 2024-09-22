package mm.com.mytelpay.family.business.account.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.RoleType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionAccountDTO {

    private String roleId;
    private RoleType roleCode;
    private String roleName;
    private boolean checked = false;

    public PermissionAccountDTO(String roleId, RoleType roleCode, String roleName) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
    }
}
