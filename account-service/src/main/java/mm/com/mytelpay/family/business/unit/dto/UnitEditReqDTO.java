package mm.com.mytelpay.family.business.unit.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@Data
public class UnitEditReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 100)
    private String id;

    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @NotBlank
    @SizeRegex(max = 50)
    private String code;

}
