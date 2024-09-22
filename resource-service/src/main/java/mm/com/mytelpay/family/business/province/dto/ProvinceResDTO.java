package mm.com.mytelpay.family.business.province.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceResDTO {
    private String name;
    private String description;
    private String code;
    private LocalDateTime createdAt;
}
