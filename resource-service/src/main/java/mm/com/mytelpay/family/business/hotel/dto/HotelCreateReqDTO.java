package mm.com.mytelpay.family.business.hotel.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.NumberRegex;
import mm.com.mytelpay.family.exception.validate.PositiveNumberRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;

@Data
public class HotelCreateReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    @NotBlank
    @SizeRegex(max = 50)
    private String code;

    @NotBlank
    private String provinceId;

    @NotBlank
    private String districtId;

    @NotBlank
    @SizeRegex(max = 255)
    private String address;

    @NotBlank
    @NumberRegex
    @SizeRegex(min = 8, max = 15)
    private String phone;

    @SizeRegex(max = 255)
    private String description;

    @NotBlank
    private List<String> rolesAllow;

    @SizeRegex(min = 1, max = 5)
    private Integer rating;

    @NotBlank
    @PositiveNumberRegex
    @SizeRegex(max = 9)
    private String maxPrice;

    @NotBlank
    @PositiveNumberRegex
    @SizeRegex(max = 9)
    private String maxPlusPrice;

}
