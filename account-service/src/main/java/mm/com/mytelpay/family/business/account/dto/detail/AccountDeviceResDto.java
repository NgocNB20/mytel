package mm.com.mytelpay.family.business.account.dto.detail;

import lombok.Data;
import mm.com.mytelpay.family.enums.OsType;

@Data
public class AccountDeviceResDto {

    private String deviceId;

    private OsType os;

}
