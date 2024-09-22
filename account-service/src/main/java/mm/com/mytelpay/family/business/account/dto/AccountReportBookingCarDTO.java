package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountReportBookingCarDTO extends BaseRequest {
    private String accountId;
    private String fullName;
    private String phone;
    private String avtUrl;

    public AccountReportBookingCarDTO(String accountId, String fullName, String phone) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.phone = phone;
    }
}
