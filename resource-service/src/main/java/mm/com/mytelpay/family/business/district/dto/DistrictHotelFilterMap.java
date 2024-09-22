package mm.com.mytelpay.family.business.district.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistrictHotelFilterMap {
    private String hotelId;
    private String districtCode;
    private String districtName;
}
