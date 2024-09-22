package mm.com.mytelpay.family.business.account.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.model.entities.AccountInfo;
import mm.com.mytelpay.family.model.entities.Role;
import mm.com.mytelpay.family.model.entities.Unit;
import org.keycloak.representations.AccessTokenResponse;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResDTO {

    private AccessTokenResponse accessTokenResponse;

    private LoginAccountInfoResDTO accountInfo;

    public static LoginResDTO mapper(AccessTokenResponse accessTokenResponse, Account account, AccountInfo accountInfo, List<Role> roles, Unit unit, FileResponse avatar) {
        LoginResDTO response = new LoginResDTO();
        LoginAccountInfoResDTO accountInfoResDTO = LoginAccountInfoResDTO.mapper(account, accountInfo, roles, unit, avatar);
        response.setAccessTokenResponse(accessTokenResponse);
        response.setAccountInfo(accountInfoResDTO);
        return response;
    }

}
