package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;


@Data
@Getter
@Setter
public class RejectBookingCarReqDTO extends BaseRequest {

    @NotBlank
    private String bookingId;

    @NotBlank
    private String roleId;

    @NotBlank
    private String reason;
}
