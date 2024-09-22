package mm.com.mytelpay.family.business.province.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.model.Province;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceImportErrorDTO {
    private Province province;
    private String res;
}
