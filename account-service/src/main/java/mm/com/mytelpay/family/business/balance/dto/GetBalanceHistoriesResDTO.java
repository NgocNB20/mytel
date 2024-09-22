package mm.com.mytelpay.family.business.balance.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BalanceActionType;
import mm.com.mytelpay.family.utils.BasePagination;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetBalanceHistoriesResDTO extends BasePagination {

    private String from;

    private String to;

    private String userPhone;

    private Integer amount;

    private LocalDateTime createdAt;

    private BalanceActionType type;

    public GetBalanceHistoriesResDTO(String from, String to, String userPhone, Integer amount, LocalDateTime createdAt, BalanceActionType type) {
        this.userPhone = userPhone;
        this.amount = amount;
        this.createdAt = createdAt;
        this.type = type;
        this.from = BalanceActionType.REFUND.equals(type) ? "Mytel Family" : from;
        this.to = to;
    }

}
