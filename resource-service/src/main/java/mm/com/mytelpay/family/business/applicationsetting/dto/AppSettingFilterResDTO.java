package mm.com.mytelpay.family.business.applicationsetting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSettingFilterResDTO {

    private String id;

    private String key;

    private String value;

    private Status status;
}
