package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.*;
import mm.com.mytelpay.family.business.bookingcar.dto.AccountRoleResDto;
import mm.com.mytelpay.family.enums.Status;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private String accountId;

    private String email;

    private String msisdn;

    private String unitId;

    private String unitName;

    private String name;

    private List<AccountRoleResDto> roles;

    private Integer balance;

    private Status status;
    private String canteenId;
}
