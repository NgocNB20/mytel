package mm.com.mytelpay.family.business.applicationsetting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSettingDetailResDTO {

    private String id;

    private String key;

    private String value;

    private Status status;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;
}
