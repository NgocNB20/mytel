package mm.com.mytelpay.family.business.applicationsetting.dto;

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
public class AppSettingCreateReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 255)
    private String key;

    @NotBlank
    private String value;

    private String description;

}
