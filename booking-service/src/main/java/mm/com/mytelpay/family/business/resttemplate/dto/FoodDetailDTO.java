package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.FoodType;

import java.util.List;

@Data
public class FoodDetailDTO {

    private String id;

    private String name;

    private FoodType type;

    private List<FileResponse> files;
}
