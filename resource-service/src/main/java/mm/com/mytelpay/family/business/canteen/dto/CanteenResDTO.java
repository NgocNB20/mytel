package mm.com.mytelpay.family.business.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CanteenResDTO {
    private String name;
    private String unitId;

    private Integer seats;

    private Status status;
}
