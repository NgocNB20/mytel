package mm.com.mytelpay.family.business.account.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VerifiedOtpResponse {

    private String originalRequestId;

    private String accountId;

}
