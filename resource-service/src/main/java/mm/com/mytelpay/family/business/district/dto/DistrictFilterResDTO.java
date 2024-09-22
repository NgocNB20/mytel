package mm.com.mytelpay.family.business.district.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictFilterResDTO extends BasePagination {
    private String name;
    private String code;
    private String provinceId;
}
