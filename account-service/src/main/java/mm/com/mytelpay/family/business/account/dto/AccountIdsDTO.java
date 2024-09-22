package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountIdsDTO extends BaseRequest {
    private List<String> accountId;
}
