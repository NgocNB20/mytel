package mm.com.mytelpay.family.business.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanteenForChefReqDTO extends BaseRequest {
    private String unitId;
}
