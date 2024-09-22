package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitForChefDTO extends BaseRequest {
    private String unitId;
}
