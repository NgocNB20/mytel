package mm.com.mytelpay.family.business.unit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitForCanteenDTO extends BaseRequest {
    private String id;
    private String name;
    private String code;
}
