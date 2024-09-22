package mm.com.mytelpay.family.business.applicationsetting.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;

@Data
public class AppSettingGetKeyDTO extends BaseRequest {
    @NotBlank
    private String key;
}
