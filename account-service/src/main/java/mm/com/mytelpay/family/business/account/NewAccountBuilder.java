package mm.com.mytelpay.family.business.account;

import mm.com.mytelpay.family.business.account.dto.AccountImportReqDTO;
import mm.com.mytelpay.family.business.account.dto.AddAccountReqDTO;
import mm.com.mytelpay.family.business.account.dto.UserRegisterReqDTO;
import mm.com.mytelpay.family.enums.OsType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.model.entities.AccountInfo;
import mm.com.mytelpay.family.model.entities.Device;
import mm.com.mytelpay.family.model.entities.Unit;
import mm.com.mytelpay.family.repo.UnitRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewAccountBuilder {
    private final UnitRepository unitRepository;

    public NewAccountBuilder(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public static Account newAccountBuilder(final UserRegisterReqDTO request) {
        return Account.builder()
                .id(UUID.randomUUID().toString())
                .msisdn(request.getMsisdn())
                .email(request.getEmail())
                .unitId(request.getUnitId())
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static AccountInfo newAccountInfoBuilder(final UserRegisterReqDTO request, final String accountId) {
        return AccountInfo.builder()
                .accountId(accountId)
                .currentBalance(0)
                .fullName(request.getFullName())
                .build();
    }

    public static Device newDeviceBuilder(final UserRegisterReqDTO request, final String accountId) {
        return Device.builder()
                .accountId(accountId)
                .deviceId(request.getDeviceId())
                .osType(OsType.valueOf(request.getOs()))
                .build();
    }

    public static Account newAccountBuilder(final AddAccountReqDTO request) {
        return Account.builder()
                .id(UUID.randomUUID().toString())
                .msisdn(request.getPhone())
                .email(request.getEmail())
                .unitId(request.getUnitId())
                .status(Status.ACTIVE)
                .canteenId(request.getCanteenId())
                .build();
    }

    public Account newAccountBuilder(AccountImportReqDTO reqDTO){
        Unit unit = unitRepository.findUnitByCode(reqDTO.getUnitCode()).orElse(null);

        Account account = new Account();
        account.setId(UUID.randomUUID().toString());
        account.setMsisdn(reqDTO.getPhone());
        account.setEmail(reqDTO.getEmail());
        account.setStatus(Status.ACTIVE);
        account.setUnitId(unit != null ? unit.getId() : null);
        return account;
    }

    public static AccountInfo newAccountInfoBuilder(final AddAccountReqDTO request, final String accountId) {
        return AccountInfo.builder()
                .accountId(accountId)
                .fullName(request.getName())
                .currentBalance(0)
                .build();
    }

    public static AccountInfo newAccountInfoBuider(AccountImportReqDTO reqDTO , String accountId){
        return AccountInfo.builder()
                .accountId(accountId)
                .fullName(reqDTO.getName())
                .currentBalance(0)
                .build();
    }

}
