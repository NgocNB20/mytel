package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Table(name = "BOOKING_CAR_HISTORY")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Data
public class BookingCarHistory extends BaseBookingHistory{

    @Column(name = "BOOKING_CAR_ID", columnDefinition = "VARCHAR(36)")
    private String bookingCarId;

}