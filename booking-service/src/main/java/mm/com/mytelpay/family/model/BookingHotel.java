package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "BOOKING_HOTEL")
@Data
public class BookingHotel extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "HOTEL_ID", columnDefinition = "VARCHAR(36)")
    private String hotelId;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Column(name = "MEMBER", columnDefinition = "NUMERIC(5)")
    private Integer member;

    @Column(name = "FROM_TIME", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime fromTime;

    @Column(name = "TO_TIME", columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime toTime;

    @Column(name = "REASON", columnDefinition = "VARCHAR(255)")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "BOOKING_STATUS", columnDefinition = "VARCHAR(50)")
    private BookingStatus bookingStatus;

    @Column(name = "FEE_BOOKING", columnDefinition = "NUMERIC(10)")
    private Integer feeBooking;

    @Column(name = "FEE_SERVICE", columnDefinition = "NUMERIC(10)")
    private Integer feeService;

    @Column(name = "NOTE", columnDefinition = "VARCHAR(255)")
    private String note;

}
