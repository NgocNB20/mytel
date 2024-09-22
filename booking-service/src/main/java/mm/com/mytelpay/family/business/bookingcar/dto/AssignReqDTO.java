package mm.com.mytelpay.family.business.bookingcar.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@Getter
@Setter
public class AssignReqDTO extends BaseRequest {

    private String timeStart;
    private String timeReturn;

}
