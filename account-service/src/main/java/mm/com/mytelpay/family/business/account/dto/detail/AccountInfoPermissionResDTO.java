package mm.com.mytelpay.family.business.account.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoPermissionResDTO {
    private String name;
    private String msisdn;
    private String unitId;
    private String unitCode;
    private String unitName;
    private List<PermissionAccountDTO> lstPer;

    public AccountInfoPermissionResDTO(String name, String msisdn, String unitId, String unitCode, String unitName) {
        this.name = name;
        this.msisdn = msisdn;
        this.unitId = unitId;
        this.unitCode = unitCode;
        this.unitName = unitName;
    }
}
