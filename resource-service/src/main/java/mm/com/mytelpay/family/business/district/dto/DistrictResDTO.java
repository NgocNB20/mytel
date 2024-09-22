package mm.com.mytelpay.family.business.district.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictResDTO {
    private String id;
    @SizeRegex(max = 100)
    private String name;
    @SizeRegex(max = 255)
    private String description;
    private String provinceCode;
    private String provinceName;
    private String provinceId;
    @SizeRegex(max = 50)
    private String code;
}
