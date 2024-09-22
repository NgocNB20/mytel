package mm.com.mytelpay.family.business.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelImportDTO {
    private String code;
    private String name;
    private String phone;
    private String address;
    private String rolesAllow;
    private String rating;
    private String districtCode;
    private String provinceCode;
    private String maxPrice;
    private String maxPlusPrice;
    private String description;
}
