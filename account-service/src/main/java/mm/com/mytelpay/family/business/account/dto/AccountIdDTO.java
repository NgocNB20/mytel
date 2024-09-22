package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountIdDTO extends BaseRequest {
    private String id;
}
