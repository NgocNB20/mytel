package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.utils.FileResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FoodResourceDTO {

    private String id;

    private String name;

    private FoodType type;

    private LocalDateTime createdAt;

    private List<FileResponse> files;
}
