package mm.com.mytelpay.family.business.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.RoleType;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRoleResDTO {

    private String id;
    private String name;
    private RoleType code;

}
