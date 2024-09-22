package mm.com.mytelpay.family.business.role.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditRoleReqDTO extends BaseRequest {
    @NotBlank
    @SizeRegex(max = 100)
    private String id;
    @NotBlank
    @SizeRegex(max = 100)
    private String name;

}
