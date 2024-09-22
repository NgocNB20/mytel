package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetListMenuReqDTO {

    private String day;

    private String canteenId;

    private String requestId;

}
