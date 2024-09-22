package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterAccountResponse {

    private String accountId;

    private String name;

    private String email;

    private String phone;

    private String unitId;

    private List<String> roleCodes;

    private Status status;

}
