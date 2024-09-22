package mm.com.mytelpay.family.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.models.BaseModel;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACCOUNT_ROLE")
@SuperBuilder(toBuilder = true)
public class AccountRole extends BaseModel {

    public AccountRole(String accountId, String roleId) {
        this.accountId = accountId;
        this.roleId = roleId;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    private Integer id;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Column(name = "ROLE_ID", columnDefinition = "VARCHAR(36)")
    private String roleId;

}
