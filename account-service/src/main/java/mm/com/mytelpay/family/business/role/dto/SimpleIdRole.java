package mm.com.mytelpay.family.business.role.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SimpleIdRole extends BaseRequest {
    @NotBlank
    @SizeRegex(max = 100)
    private String roleId;
}
