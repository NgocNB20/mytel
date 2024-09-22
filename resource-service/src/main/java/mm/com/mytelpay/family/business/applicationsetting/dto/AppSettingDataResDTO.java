package mm.com.mytelpay.family.business.applicationsetting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSettingDataResDTO {
    private String subject;

    private String content;
}
