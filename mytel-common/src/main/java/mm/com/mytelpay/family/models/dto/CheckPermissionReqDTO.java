package mm.com.mytelpay.family.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckPermissionReqDTO {

    private String bearToken;
    private String endpoint;

}
