package mm.com.mytelpay.family.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity(name = "APPLICATION_SETTING")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplicationSetting extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "APP_KEY", columnDefinition = "VARCHAR(255)")
    private String key;

    @Column(name = "VALUE", columnDefinition="TEXT")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", columnDefinition = "VARCHAR(36)")
    private Status status = Status.ACTIVE;

    @Column(name = "DESCRIPTION" , columnDefinition = "VARCHAR(255)")
    private String description;

}
