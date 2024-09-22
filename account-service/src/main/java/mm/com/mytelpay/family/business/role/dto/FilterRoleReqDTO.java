package mm.com.mytelpay.family.business.role.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRoleReqDTO extends BasePagination {
    @SizeRegex(max = 100)
    private String name;
    @SizeRegex(max = 50)
    @EnumRegex(enumClass = RoleType.class)
    private String code;

}
