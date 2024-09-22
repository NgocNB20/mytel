package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckCanteenForChefResDTO extends BaseRequest {
    private String canteenId;
    private String canteenName;
}
