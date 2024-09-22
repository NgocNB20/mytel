package mm.com.mytelpay.family.business.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CanteenDetailResDTO {
    private String id;
    private String name;
    private String unitId;
    private String address;
    private Integer seats;
    private String code;
    private String description;
    private String unitName;
}
