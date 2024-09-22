package mm.com.mytelpay.family.model.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.OsType;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "DEVICE")
public class Device extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "DEVICE_ID", columnDefinition = "VARCHAR(255)")
    private String deviceId;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "OS_TYPE", columnDefinition = "VARCHAR(36)")
    private OsType osType;

    @Column(name = "LANGUAGE", columnDefinition = "VARCHAR(36)")
    private String lang;

    public Device(String deviceId, String accountId, OsType osType, String lang) {
        this.deviceId = deviceId;
        this.accountId = accountId;
        this.osType = osType;
        this.lang = lang;
    }
}
