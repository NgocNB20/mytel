package mm.com.mytelpay.family.business.account.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.account.dto.FunctionResDTO;
import mm.com.mytelpay.family.model.entities.Role;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRoleResDTO {

    private String roleId;

    private String code;

    private String name;

    private List<FunctionResDTO> permissions;

    public static LoginRoleResDTO mapper(Role role) {
        LoginRoleResDTO loginRoleResDTO = new LoginRoleResDTO();
        loginRoleResDTO.setRoleId(role.getId());
        loginRoleResDTO.setCode(role.getCode().toString());
        loginRoleResDTO.setName(role.getName());
        if (CollectionUtils.isNotEmpty(role.getFunctions())) {
            List<FunctionResDTO> permissionMaps = role.getFunctions().stream()
                    .map(function -> new FunctionResDTO(function.getId(), function.getParentId(), function.getCode(), function.getName()))
                    .collect(Collectors.toList());
            loginRoleResDTO.setPermissions(permissionMaps);
        }
        return loginRoleResDTO;
    }

}
