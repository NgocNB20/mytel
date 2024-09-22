package mm.com.mytelpay.family.business.hotel.dto;

import lombok.Data;
import mm.com.mytelpay.family.exception.validate.SizeRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
public class HotelExportReqDTO extends BaseRequest {

    @SizeRegex(max = 100)
    private String name;

    private String provinceId;

    private String districtId;

    @SizeRegex(min = 1, max = 5)
    private Integer rating;

    private String code;
}
