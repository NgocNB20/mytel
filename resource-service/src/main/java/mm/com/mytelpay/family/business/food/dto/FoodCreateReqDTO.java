package mm.com.mytelpay.family.business.food.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.MultipartFileRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FoodCreateReqDTO{

    @NotBlank
    private String name;

    @NotBlank
    @EnumRegex(enumClass = FoodType.class)
    private String type;

    @NotBlank
    @MultipartFileRegex
    private MultipartFile file;
}
