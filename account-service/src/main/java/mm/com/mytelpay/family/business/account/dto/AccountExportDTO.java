package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountExportDTO {
    private String name;

    private String msisdn;

    private String email;
    private String unitCode;
    @EnumRegex(enumClass = Status.class)
    private String status;

}
