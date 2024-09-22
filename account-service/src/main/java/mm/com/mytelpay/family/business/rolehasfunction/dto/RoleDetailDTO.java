package mm.com.mytelpay.family.business.rolehasfunction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.RoleType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDetailDTO {
    private String roleId;
    private RoleType roleCode;
    private String roleName;
    private List<FunctionPerRole> perRole;

}
