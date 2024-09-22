package mm.com.mytelpay.family.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.BalanceActionType;
import mm.com.mytelpay.family.models.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "ACCOUNT_BALANCE_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceHistory extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    private Long id;

    @Column(name = "CREATED_BY", columnDefinition = "VARCHAR(36)")
    private String createdBy;

    @Column(name = "SUBJECT_ID", columnDefinition = "VARCHAR(36)")
    private String subjectId;

    @Column(name = "SUBJECT_PHONE", columnDefinition = "VARCHAR(36)")
    private String subjectPhone;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Column(name = "OLD_BALANCE")
    private Integer oldBalance;

    @Column(name = "ACTION_TYPE")
    @Enumerated(EnumType.STRING)
    private BalanceActionType actionType;

}
