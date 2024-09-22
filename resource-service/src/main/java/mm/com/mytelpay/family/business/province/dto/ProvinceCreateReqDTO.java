package mm.com.mytelpay.family.business.province.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceCreateReqDTO extends BaseRequest {
    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @SizeRegex(max = 255)
    private String description;

    @NotBlank
    @SizeRegex(max = 50)
    private String code;
}
