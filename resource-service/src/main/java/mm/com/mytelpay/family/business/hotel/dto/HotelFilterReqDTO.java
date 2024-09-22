package mm.com.mytelpay.family.business.hotel.dto;

import lombok.Data;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
public class HotelFilterReqDTO extends BasePagination {

    private String name;

    private String code;

    private String provinceId;

    private String districtId;

    private Integer rating;

}
