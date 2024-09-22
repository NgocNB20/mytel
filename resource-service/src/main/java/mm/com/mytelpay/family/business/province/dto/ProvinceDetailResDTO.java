package mm.com.mytelpay.family.business.province.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceDetailResDTO {
    private String id;
    private String name;
    private String description;
    @EnumRegex(enumClass = Status.class)
    private String status;
    private String code;
    private LocalDateTime createAt;
}
