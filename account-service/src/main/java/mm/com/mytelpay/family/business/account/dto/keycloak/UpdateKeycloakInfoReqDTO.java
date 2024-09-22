package mm.com.mytelpay.family.business.account.dto.keycloak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateKeycloakInfoReqDTO {

    private String fullName;

    private String email;

}
