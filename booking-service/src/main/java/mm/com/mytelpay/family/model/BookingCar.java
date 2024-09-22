package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "BOOKING_CAR")
@Data
public class BookingCar extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "TIME_START", columnDefinition = "DATETIME")
    private LocalDateTime timeStart;

    @Column(name = "TIME_RETURN", columnDefinition = "DATETIME")
    private LocalDateTime timeReturn;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE_BOOKING", columnDefinition = "VARCHAR(255)")
    private CarBookingType typeBooking;

    @Column(name = "QUANTITY", columnDefinition = "INTEGER")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "BOOKING_STATUS", columnDefinition = "VARCHAR(50)")
    private BookingStatus bookingStatus;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Column(name = "NOTE", columnDefinition = "VARCHAR(255)")
    private String note;

    @Column(name = "REASON", columnDefinition = "VARCHAR(255)")
    private String reason;

    @Column(name = "ORIGINAL", columnDefinition = "VARCHAR(255)")
    private String original;

    @Column(name = "DESTINATION", columnDefinition = "VARCHAR(255)")
    private String destination;

    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "APPROVE_LEVEL")
    private Integer approveLevel;

    @Column(name = "UNIT_ID", columnDefinition = "VARCHAR(36)")
    private String unitId;

    @Column(name = "FUEL_ESTIMATE", columnDefinition = "DOUBLE")
    private Double fuelEstimate;

    @Column(name = "DISTANCE", columnDefinition = "DOUBLE")
    private Double distance;
}
