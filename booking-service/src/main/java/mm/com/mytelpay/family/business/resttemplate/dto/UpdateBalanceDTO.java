package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BalanceActionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceDTO {

    private Integer amount;

    private BalanceActionType actionType;

}
