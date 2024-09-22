package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mm.com.mytelpay.family.enums.CarBookingDetailStatus;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import mm.com.mytelpay.family.exception.validate.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UpdateBookingCarReqDTO extends BaseRequest {

    @NotBlank
    private String bookingDetailId;

    @NotNull
    private CarBookingDetailStatus status;
}
