package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Table(name = "BOOKING_HOTEL_HISTORY")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Data
public class BookingHotelHistory extends BaseBookingHistory{

    @Column(name = "BOOKING_HOTEL_ID", columnDefinition = "VARCHAR(36)")
    private String bookingHotelId;

}