package mm.com.mytelpay.family.business.account.dto.login;

import lombok.Data;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.model.entities.AccountInfo;
import mm.com.mytelpay.family.model.entities.Role;
import mm.com.mytelpay.family.model.entities.Unit;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class LoginAccountInfoResDTO {

    private String accountId;

    private String phoneNumber;

    private String name;

    private String email;

    private String unit;

    private String unitId;

    private Integer balance;

    private String infoStatus;

    private String driverLicense;

    private List<LoginRoleResDTO> roles;

    private FileResponse avatar;

    public static LoginAccountInfoResDTO mapper(Account account, AccountInfo accountInfo, List<Role> roles, Unit unit, FileResponse avatar) {
        LoginAccountInfoResDTO loginAccountInfoResDTO = new LoginAccountInfoResDTO();
        loginAccountInfoResDTO.setAccountId(account.getId());
        loginAccountInfoResDTO.setEmail(account.getEmail());
        loginAccountInfoResDTO.setPhoneNumber(account.getMsisdn());
        loginAccountInfoResDTO.setInfoStatus(account.getStatus().toString());
        if (accountInfo != null) {
            loginAccountInfoResDTO.setName(accountInfo.getFullName());
            loginAccountInfoResDTO.setDriverLicense(accountInfo.getDriverLicense());
            loginAccountInfoResDTO.setBalance(accountInfo.getCurrentBalance());
        }
        loginAccountInfoResDTO.setUnit(unit != null ? unit.getName() : null);
        loginAccountInfoResDTO.setUnitId(unit != null ? unit.getId() : null);
        if (CollectionUtils.isNotEmpty(roles)) {
            List<LoginRoleResDTO> roleList = roles.stream()
                    .map(LoginRoleResDTO::mapper)
                    .collect(Collectors.toList());
            loginAccountInfoResDTO.setRoles(roleList);
        }
        loginAccountInfoResDTO.setAvatar(avatar);
        return loginAccountInfoResDTO;
    }

}
