package mm.com.mytelpay.family.business.menu.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
public class MenuFilterReqDto extends BasePagination {

    @EnumRegex(enumClass = Day.class)
    private String day;

    private String canteenId;
}
