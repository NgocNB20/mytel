package mm.com.mytelpay.family.business.hotel.dto;

import lombok.Data;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;

@Data
public class GetHotelsByIdsReqDTO extends BaseRequest {

    private List<String> ids;

}
