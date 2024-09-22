package mm.com.mytelpay.family.business.rolehasfunction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerRoleDTO extends BaseRequest {
    @NotBlank
    @SizeRegex(max = 100)
    private String roleId;
    private List<String> functionIdList;
}
