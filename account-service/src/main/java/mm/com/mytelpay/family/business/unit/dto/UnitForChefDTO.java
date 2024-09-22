package mm.com.mytelpay.family.business.unit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitForChefDTO extends BaseRequest {
    @NotBlank
    private String unitId;
}
