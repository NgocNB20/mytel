package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignIdsReqDTO extends BaseRequest {
    private List<String> ids;
}
