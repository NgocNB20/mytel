package mm.com.mytelpay.family.business.food.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;

@Data
public class FoodCreateArrayReqDTO extends BaseRequest {

    @NotBlank
    private List<FoodCreateReqDTO> foods;
}
