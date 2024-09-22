package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculatorDistanceReqDTO extends BaseRequest {

    private double fromLongitude;

    private double fromLatitude;

    private double toLongitude;

    private double toLatitude;
}
