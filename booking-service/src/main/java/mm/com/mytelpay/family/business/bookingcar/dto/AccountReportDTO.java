package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountReportDTO extends BaseRequest {
    private String accountId;
    private String fullName;
    private String phone;
    private String avtUrl;
}
