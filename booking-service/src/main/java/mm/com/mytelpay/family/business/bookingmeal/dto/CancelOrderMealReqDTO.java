package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
public class CancelOrderMealReqDTO extends BaseRequest {

    @NotBlank
    private String bookingDetailId;

    @NotBlank
    private String reason;

}
