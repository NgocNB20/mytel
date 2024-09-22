package mm.com.mytelpay.family.business.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NumberRegex;
import mm.com.mytelpay.family.exception.validate.PositiveNumberRegex;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelEditReqDTO extends BaseRequest {

    private String id;

    @SizeRegex(max = 100)
    private String name;

    @SizeRegex(max = 50)
    private String code;

    private String provinceId;

    private String districtId;

    @SizeRegex(max = 255)
    private String address;

    @NumberRegex
    @SizeRegex(min = 8, max = 15)
    private String phone;

    @SizeRegex(max = 255)
    private String description;

    private List<String> rolesAllow;

    @SizeRegex(min = 1, max = 5)
    private Integer rating;

    @PositiveNumberRegex
    @SizeRegex(max = 9)
    private String maxPrice;

    @PositiveNumberRegex
    @SizeRegex(max = 9)
    private String maxPlusPrice;

    private List<Long> fileDeletes;

}
