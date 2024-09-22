package mm.com.mytelpay.family.business.province.dto;

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
public class ProvinceEditReqDTO extends BaseRequest {
    @NotBlank
    private String id;

    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @SizeRegex(max = 255)
    private String description;

    @NotBlank
    @SizeRegex(max = 50)
    private String code;
}
