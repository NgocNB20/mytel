package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingAssignDriverDTO {
    List<DriverDTO> lstDriverOutbound;
    List<DriverDTO> lstDriverReturn;

}
