package mm.com.mytelpay.family.business.role.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private String roleId;

    private String name;

    private String code;

    private List<String> permissions;

}
