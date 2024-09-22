package mm.com.mytelpay.family.business.district.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictFilterReqDTO {
    private String id;
    private String code;
    private String name;
    private String provinceId;
    private String provinceName;
    private String provinceCode;
    private String description;
}
