package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;


@Data
@Getter
@Setter
public class ApproveBookingCarReqDTO extends BaseRequest {

    @NotBlank
    private String bookingId;

    @NotBlank
    private String roleId;

    private String driverOutboundId;

    private String carOutboundId;

    private String driverReturnId;

    private String carReturnId;
}
