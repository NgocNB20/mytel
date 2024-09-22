package mm.com.mytelpay.family.business.menu.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import javax.validation.Valid;
import java.util.List;

@Data
public class MenuCreateReqDTO extends BaseRequest {
    @NotBlank
    private String name;
    @EnumRegex(enumClass = Day.class)
    private String day;
    @Valid
    private List<MealListDto> listMeal;
}
