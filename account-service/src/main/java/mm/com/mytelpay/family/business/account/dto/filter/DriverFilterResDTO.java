package mm.com.mytelpay.family.business.account.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverFilterResDTO {
    private String id;
    private String name;
    private String msisdn;
    private String email;
    private String driverLicense;
    private Status status;

}
