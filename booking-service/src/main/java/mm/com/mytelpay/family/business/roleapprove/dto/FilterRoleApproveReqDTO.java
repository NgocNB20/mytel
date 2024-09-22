package mm.com.mytelpay.family.business.roleapprove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BookingType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRoleApproveReqDTO {

    @EnumRegex(enumClass = BookingType.class)
    private String type;

    private Boolean isAssign;

    private Integer level;

    private String roleId;

}
