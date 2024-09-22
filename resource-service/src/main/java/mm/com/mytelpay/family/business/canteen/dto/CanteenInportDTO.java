package mm.com.mytelpay.family.business.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanteenInportDTO extends BaseRequest {
    private String code;
    private String name;
    private String unit;
    private String seat;
    private String address;
    private String description;
}
