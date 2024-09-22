package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.CarBookingDetailStatus;
import mm.com.mytelpay.family.enums.DirectionType;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "BOOKING_CAR_DETAIL")
@Data
public class BookingCarDetail extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "BOOKING_CAR_ID", columnDefinition = "VARCHAR(36)")
    private String bookingCarId;

    @Column(name = "TIME_START", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime timeStart;

    @Column(name = "STATUS", columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private CarBookingDetailStatus status;

    @Column(name = "TYPE", columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private DirectionType type;

    @Column(name = "ORIGINAL", columnDefinition = "VARCHAR(255)")
    private String original;

    @Column(name = "DESTINATION", columnDefinition = "VARCHAR(255)")
    private String destination;

    @Column(name = "CAR_ID", columnDefinition = "VARCHAR(100)")
    private String carId;

    @Column(name = "DRIVER_ID", columnDefinition = "VARCHAR(100)")
    private String driverId;
}
