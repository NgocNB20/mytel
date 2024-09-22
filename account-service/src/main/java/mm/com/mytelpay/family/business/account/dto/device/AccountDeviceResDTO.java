package mm.com.mytelpay.family.business.account.dto.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class AccountDeviceResDTO {

    private String deviceId;

    private String accountId;

    private String lang;

}
