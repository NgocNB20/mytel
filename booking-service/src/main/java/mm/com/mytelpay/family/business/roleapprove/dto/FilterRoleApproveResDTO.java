
package mm.com.mytelpay.family.business.roleapprove.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRoleApproveResDTO {

    private String id;

    private String bookingType;

    private String roleId;

    private Integer level;

    private Integer isAssign;

    private String description;


}
