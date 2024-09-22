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
public class CanteenEditReqDTO extends BaseRequest {
    @NotBlank
    private String canteenId;

    @SizeRegex(max = 50)
    private String code;

    @SizeRegex(max = 100)
    private String name;

    @NumberRegex
    @SizeRegex(max = 5)
    private String numberSeats;

    private String address;

    private String description;

}
