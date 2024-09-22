package mm.com.mytelpay.family.business.applicationsetting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSettingEditReqDTO extends BaseRequest {

    @NotBlank
    private String id;

    @SizeRegex(max = 255)
    private String key;

    private String value;

    @SizeRegex(max = 255)
    private String description;

    @EnumRegex(enumClass = Status.class)
    private String status;
}
