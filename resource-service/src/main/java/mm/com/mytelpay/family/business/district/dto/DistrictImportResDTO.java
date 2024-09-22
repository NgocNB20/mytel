package mm.com.mytelpay.family.business.district.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictImportResDTO {
    private String id;
    private String name;
    private String description;
    private String provinceCode;
    private String code;
}
