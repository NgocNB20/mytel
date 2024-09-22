package mm.com.mytelpay.family.business.account.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.account.dto.login.LoginRoleResDTO;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.enums.Status;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDetailResDTO {
    private String accountId;
    private String phoneNumber;
    private String name;
    private String email;
    private String unit;
    private String unitId;
    private Status infoStatus;
    private String driverLicense;
    private Integer balance;

    private List<FileResponse> avatar;

    private List<LoginRoleResDTO> roles;
    private String canteenId;
    private String canteenName;
    private Boolean carRegistrationActive;

    public ProfileDetailResDTO(String accountId, String phoneNumber, String name, String email, String unit, String unitId, Status infoStatus,String canteenId, String driverLicense, Integer balance) {
        this.accountId = accountId;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
        this.unit = unit;
        this.unitId = unitId;
        this.infoStatus = infoStatus;
        this.canteenId = canteenId;
        this.driverLicense = driverLicense;
        this.balance = balance;
    }
}
