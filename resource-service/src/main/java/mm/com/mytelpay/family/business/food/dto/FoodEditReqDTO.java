package mm.com.mytelpay.family.business.food.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.MultipartFileRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FoodEditReqDTO extends BaseRequest {

    @NotBlank
    private String id;

    private String name;

    @EnumRegex(enumClass = FoodType.class)
    private String type;

    @MultipartFileRegex
    private MultipartFile file;

}
