package mm.com.mytelpay.family.business.account.dto.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class AccountsInfoRequest extends BaseRequest {

    private List<String> accountIds;
}
