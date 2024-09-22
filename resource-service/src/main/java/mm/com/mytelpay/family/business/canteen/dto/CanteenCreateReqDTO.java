package mm.com.mytelpay.family.business.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.NumberRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanteenCreateReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 50)
    private String code;

    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @NotBlank
    private String unit;

    @NotBlank
    @NumberRegex
    @SizeRegex(max = 5)
    private String numberSeats;

    private String address;

    private String description;

}
