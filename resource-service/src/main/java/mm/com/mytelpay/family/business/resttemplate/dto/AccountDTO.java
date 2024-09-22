package mm.com.mytelpay.family.business.resttemplate.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mm.com.mytelpay.family.enums.Status;

import java.util.List;

@Data
@Getter
@Setter
public class AccountDTO {

    private String accountId;

    private String email;

    private String msisdn;

    private String unitId;

    private String name;

    private List<AccountRoleResDto> roles;

    private String note;

    private String deviceId;

    private String os;

    private String lang;
    
    private Status status;
    
    private String canteenId;
}
