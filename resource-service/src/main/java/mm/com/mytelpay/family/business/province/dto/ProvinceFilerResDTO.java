package mm.com.mytelpay.family.business.province.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceFilerResDTO extends BasePagination {
    @SizeRegex(max = 100)
    private String name;

    @SizeRegex(max = 255)
    private String code;
}
