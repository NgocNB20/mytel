package mm.com.mytelpay.family.business.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelExportResDTO {

    private String code;

    private String name;

    private String phone;

    private String address;

    private String rolesAllow;

    private Integer rating;

    private String districtCode;

    private String provinceCode;

    private Integer maxPrice;

    private Integer maxPlusPrice;

    private String description;

}
