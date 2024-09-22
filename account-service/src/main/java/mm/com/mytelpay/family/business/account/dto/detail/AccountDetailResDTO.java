package mm.com.mytelpay.family.business.account.dto.detail;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

import java.util.List;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class AccountDetailResDTO {

    private String accountId;

    private String email;

    private String msisdn;

    private String unitId;

    private String unitName;

    private String name;

    private Status status;

    private Integer balance;

    private List<AccountRoleResDto> roles;

    private String canteenId;

    private String canteenName;
    
    private Boolean carRegistrationActive;
    
    private LocalDateTime carRegistrationTime;

    public AccountDetailResDTO(String accountId, String email, String msisdn, String name, Status status) {
        this.accountId = accountId;
        this.email = email;
        this.msisdn = msisdn;
        this.name = name;
        this.status = status;
    }
}
