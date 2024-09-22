package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import mm.com.mytelpay.family.utils.Util;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "CAR")
@Data
public class Car extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "NAME", columnDefinition = "VARCHAR(255)")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "CAR_TYPE", columnDefinition = "VARCHAR(36)")
    private CarType carType;

    @Column(name = "MODEL", columnDefinition = "VARCHAR(255)")
    private String model;

    @Column(name = "LICENSE_PLATE", columnDefinition = "VARCHAR(255)")
    private String licensePlate;

    @Column(name = "COLOR", columnDefinition = "VARCHAR(255)")
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", columnDefinition = "VARCHAR(36)")
    private Status status = Status.ACTIVE;

    @Column(name = "CREATED_BY", columnDefinition = "VARCHAR(36)")
    private String createdBy;
}
