package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterAccountRequest extends BasePagination {

    private String name;

    private String email;

    private String phone;

    private String unitId;
    @EnumRegex(enumClass = RoleType.class)
    private String roleCode;

    private Status status;

}
