package mm.com.mytelpay.family.business.applicationsetting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSettingFilterReqDTO extends BasePagination {

    private String key;

    @EnumRegex(enumClass = Status.class)
    private String status;
}
