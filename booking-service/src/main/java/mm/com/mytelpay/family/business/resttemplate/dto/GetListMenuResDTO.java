package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.Day;

import java.util.List;

@Data
public class GetListMenuResDTO {

    private Day day;

    private String name;

    private List<MealResDTO> menu;

}
