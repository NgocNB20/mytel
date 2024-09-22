package mm.com.mytelpay.family.business.province.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceHotelFilterMap {
    private String hotelId;
    private String provinceCode;
    private String provinceName;
}
