package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;


@Data
public class VerifyQrBookingMealReqDTO extends BaseRequest {

    @NotBlank
    private String bookingDetailId;

}
