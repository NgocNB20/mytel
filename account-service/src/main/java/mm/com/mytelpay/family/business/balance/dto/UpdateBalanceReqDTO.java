package mm.com.mytelpay.family.business.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BalanceActionType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.PositiveNumberRegex;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceReqDTO {

    @NotBlank
    @PositiveNumberRegex
    private String amount;

    @NotBlank
    @EnumRegex(enumClass = BalanceActionType.class)
    private String actionType;

}
