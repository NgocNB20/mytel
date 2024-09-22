package mm.com.mytelpay.family.business.bookingcar.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UpdateFuelEstBookingCarReqDTO extends BaseRequest {

    @NotBlank
    private String bookingId;

    @NotNull
    private Double fuelEstimate;
}
