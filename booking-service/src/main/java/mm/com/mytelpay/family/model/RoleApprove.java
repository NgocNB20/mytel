package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.BookingType;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "ROLE_APPROVE")
@Data
public class RoleApprove extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "BOOKING_TYPE", columnDefinition = "VARCHAR(50)")
    private BookingType bookingType ;

    @Column(name = "ROLE_ID", columnDefinition = "VARCHAR(36)")
    private String roleId;

    @Column(name = "LEVEL")
    private Integer level;

    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(100)")
    private String description;

    @Column(name = "IS_ASSIGN")
    private Boolean isAssign;

}
