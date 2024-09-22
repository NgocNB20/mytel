package mm.com.mytelpay.family.business.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BalanceActionType;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceDTO extends BaseRequest {

    private String receiverId;

    private String receiverPhone;

    private Integer amount;

    private BalanceActionType actionType;

    private String createdBy;

}
