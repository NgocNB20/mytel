package mm.com.mytelpay.family.business.unit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnitDetailResDTO {

    private String name;

    private String code;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    private String createdBy;
}
