package mm.com.mytelpay.family.business.district.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictDetailResDTO {
    private String name;
    private String description;
    private String provinceId;
    private Status status;
    private String code;
}
