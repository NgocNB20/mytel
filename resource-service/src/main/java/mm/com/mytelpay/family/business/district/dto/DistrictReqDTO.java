package mm.com.mytelpay.family.business.district.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictReqDTO extends BaseRequest {
    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @NotBlank
    @SizeRegex(max = 50)
    private String code;

    @SizeRegex(max = 255)
    private String description;

    @NotBlank
    private String provinceId;
}
