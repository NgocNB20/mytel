package mm.com.mytelpay.family.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "ACCOUNT_INFO")
public class AccountInfo extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Column(name = "FULL_NAME", columnDefinition = "VARCHAR(100)")
    private String fullName;

    @Column(name = "DRIVER_LICENSE", columnDefinition = "VARCHAR(100)")
    private String driverLicense;

    @Column(name = "CURRENT_BALANCE")
    private Integer currentBalance;

}
