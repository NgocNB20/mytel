package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@NoArgsConstructor
@Data
public class AccountDeviceReqDTO extends BaseRequest {

    private String accountId;

    private String roleId;

    private String roleCode;

    public AccountDeviceReqDTO(String accountId, String roleId, String type, String requestId) {
        this.setAccountId(accountId);
        this.setRoleId(roleId);
        this.setRoleCode(type);
        this.setRequestId(requestId);
    }
}
