package mm.com.mytelpay.family.business.account.service;

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.account.dto.*;
import mm.com.mytelpay.family.business.account.dto.detail.AccountDetailResDTO;
import mm.com.mytelpay.family.business.account.dto.detail.AccountInfoPermissionResDTO;
import mm.com.mytelpay.family.business.account.dto.detail.AccountRoleResDto;
import mm.com.mytelpay.family.business.account.dto.detail.PermissionAccountDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceReqDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountsInfoRequest;
import mm.com.mytelpay.family.business.account.dto.filter.AccountFilterReqDTO;
import mm.com.mytelpay.family.business.account.dto.filter.AccountFilterResDTO;
import mm.com.mytelpay.family.business.account.dto.filter.DriverAssignReqDTO;
import mm.com.mytelpay.family.business.account.dto.filter.DriverFilterResDTO;
import mm.com.mytelpay.family.business.account.dto.keycloak.UpdateKeycloakInfoReqDTO;
import mm.com.mytelpay.family.business.account.dto.login.LoginRoleResDTO;
import mm.com.mytelpay.family.business.account.dto.profile.ProfileDetailResDTO;
import mm.com.mytelpay.family.business.account.dto.profile.ProfileEditReqDTO;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.notification.ApplicationSettingCommonService;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.CheckCanteenForChefResDTO;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.ActionLog;
import mm.com.mytelpay.family.model.entities.*;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repo.UnitRepository;
import mm.com.mytelpay.family.transaction.AccountTransactionService;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import mm.com.mytelpay.family.utils.PasswordUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Log4j2
@AllArgsConstructor
@Service
public class AccountManageImpl extends AccountBaseBusiness implements AccountManageService {

    @Autowired
    private PasswordUtils passwordUtils;
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountExcelService accountExcelService;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private PerRequestContextDto perRequestContextDto;

    @Autowired
    private AccountTransactionService accountTransactionService;

    @Autowired
    private ApplicationSettingCommonService applicationSettingCommonService;

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;
    
    @Autowired
    private AccountSettingService accountSettingService;

    @Override
    public AccountDetailResDTO getDetails(SimpleRequest request, HttpServletRequest httpServletRequest) {
        Account account = accountRepository.findByAccountId(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ACCOUNT_ID_NOT_FOUND, null);
        });
        AccountDetailResDTO response = AccountDetailResDTO.builder()
                                                            .accountId(account.getId())
                                                            .email(account.getEmail())
                                                            .msisdn(account.getMsisdn())
                                                            .status(account.getStatus())
                                                            .unitId(account.getUnitId())
                                                            .canteenId(account.getCanteenId())
                                                            .build();
        
        AccountSettingDTO accountSetting = accountSettingService.getAccountSetting(response.getAccountId());
        
        response.setCarRegistrationActive(accountSetting.getCarRegistrationActive());
        response.setCarRegistrationTime(accountSetting.getCarRegistrationTime());
        
        if (!StringUtils.isEmpty(response.getCanteenId())) {
            CheckCanteenForChefResDTO resDTO = resourceRestTemplate.checkCanteenForChef(response.getCanteenId(),null, perRequestContextDto.getBearToken());
            response.setCanteenName(resDTO.getCanteenName());
        }
        accountInfoRepository.findByAccountId(account.getId()).ifPresent(accountInfo -> {
            response.setName(accountInfo.getFullName());
            response.setBalance(accountInfo.getCurrentBalance());
        });
        response.setRoles(roleRepository.findRoleByAccountId(account.getId())
                .stream()
                .map(role -> new AccountRoleResDto(role.getCode(), role.getName(), role.getId()))
                .collect(Collectors.toList()));

        if (StringUtils.isNotEmpty(account.getUnitId())) {
            Unit unit = unitRepository.findById(account.getUnitId()).orElse(new Unit());
            response.setUnitName(unit.getName());
        }
        return response;
    }

    @Override
    public List<LoginRoleResDTO> getAllFunctionsOfUser(String accountId, BaseRequest request, HttpServletRequest httpServletRequest) {
        Optional<Account> optionalAccount = accountRepository.findAccountIdAndStatus(accountId, Status.ACTIVE);
        if (optionalAccount.isEmpty()) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.THE_ACCOUNT_INFORMATION_IS_INCORRECT_OR_HAS_NOT_BEEN_ACTIVATED, null);
        }
        Account account = optionalAccount.get();
        List<Role> rolesOfAccount = roleRepository.findRoleByAccountId(account.getId());
        List<LoginRoleResDTO> response = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(rolesOfAccount)) {
            rolesOfAccount.forEach(role -> role.setFunctions(functionRepository.findFunctionsUsingRoleId(role.getId())));
            response = rolesOfAccount.stream()
                    .map(LoginRoleResDTO::mapper)
                    .collect(Collectors.toList());
        }
        return response;
    }

    @Override
    @SneakyThrows
    public boolean update(UpdateAccountReqDTO request, HttpServletRequest httpServletRequest) {
        Account account = accountRepository.findById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ACCOUNT_ID_NOT_FOUND, null);
        });
        this.validateBeforeUpdate(request);

        // Save account and action log
        accountTransactionService.updateAccount(request, account);
        return true;
    }

    private void validateBeforeUpdate(UpdateAccountReqDTO request) {
        Account account = accountRepository.findById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ACCOUNT_ID_NOT_FOUND, null);
        });
        Status accountStatus = account.getStatus();
        AccountInfo accountInfo = accountInfoRepository.findFirstByAccountId(account.getId());
        if (accountStatus.equals(Status.PENDING)) {
            validateUpdatePendingAccount(request);
        } else if (accountStatus.equals(Status.INACTIVE)) {
            validateUpdateInactiveAccount(request, account, accountInfo);
        } else if (accountStatus.equals(Status.ACTIVE)) {
            validateUpdateActiveAccount(request, account);
        }
    }

    private void validateUpdatePendingAccount(UpdateAccountReqDTO request) {
        throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_ALLOW_UPDATE, null);
    }

    private void validateUpdateInactiveAccount(UpdateAccountReqDTO request, Account account, AccountInfo accountInfo) {
        if (!StringUtils.equals(request.getName(), accountInfo.getFullName())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_ALLOW_UPDATE, null);
        }
        if (!StringUtils.equals(request.getPhone(), account.getMsisdn())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_ALLOW_UPDATE, null);
        }
        if (!StringUtils.equals(request.getEmail(), account.getEmail())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_ALLOW_UPDATE, null);
        }
        if (!StringUtils.equals(request.getUnitId(), account.getUnitId())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_ALLOW_UPDATE, null);
        }
        List<String> accountRoleIds = accountRoleRepository.findByAccountId(account.getId()).stream().map(AccountRole::getRoleId).collect(Collectors.toList());
        if (!this.areTwoRoleIdListsEqual(request.getRoleIdList(), accountRoleIds)) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_ALLOW_UPDATE, null);
        }
    }

    private void validateUpdateActiveAccount(UpdateAccountReqDTO request, Account account) {
        if (accountRepository.findFirstByMsisdn(request.getPhone()) == null) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.PHONE_NOT_EXISTS, null);
        }
        if (accountRepository.findFirstByEmail(request.getEmail()) == null) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.EMAIL_NOT_EXISTS, null);
        }
        Account accountByEmail = accountRepository.findFirstByEmail(request.getEmail());
        if (accountByEmail == null || !StringUtils.equals(accountByEmail.getId(), account.getId())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.PHONE_OR_EMAIL_NOT_MATCH, null);
        }
        Account accountByPhone = accountRepository.findFirstByMsisdn(request.getPhone());
        if (accountByPhone == null || !StringUtils.equals(accountByPhone.getId(), account.getId())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.PHONE_OR_EMAIL_NOT_MATCH, null);
        }
        if (unitRepository.findById(request.getUnitId()).isEmpty()) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Unit.NOT_FOUND, null);
        }
        if (CollectionUtils.isNotEmpty(request.getRoleIdList())) {
            for (String roleId : request.getRoleIdList()) {
                if (roleRepository.findById(roleId).isEmpty()) {
                    throw new BusinessEx(request.getRequestId(), AccountErrorCode.Role.ROLE_DOES_NOT_EXISTS, null);
                }
            }
        }
    }

    private boolean areTwoRoleIdListsEqual(List<String> roleIdsInput, List<String> roleIdsOfUser) {
        if (CollectionUtils.isEmpty(roleIdsInput) && CollectionUtils.isEmpty(roleIdsOfUser))
            return true;

        if (CollectionUtils.isEmpty(roleIdsInput) || CollectionUtils.isEmpty(roleIdsOfUser)) {
            return false;
        }

        if (roleIdsInput.size() != roleIdsOfUser.size()) {
            return false;
        }

        List<?> copyList = new ArrayList<>(roleIdsOfUser);

        for (Object element : roleIdsInput) {
            if (!copyList.remove(element)) {
                return false;
            }
        }

        return copyList.isEmpty();
    }

    @Override
    public boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) {
        Account account = accountRepository.findByAccountId(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ACCOUNT_ID_NOT_FOUND, null);
        });
        if (Status.PENDING.equals(account.getStatus()) || Status.ACTIVE.equals(account.getStatus())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ONLY_ALLOW_DELETING_INACTIVE_USER, null);
        }
        List<BookingCarDTO> bookingCarDTOS = checkReqBookingCarOfDriver(request.getId(), perRequestContextDto.getBearToken());
        if (CollectionUtils.isNotEmpty(bookingCarDTOS)) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.THE_ACCOUNT_CAN_ONLY_BE_DELETED_AFTER_COMPLETING_ALL_BOOKINGS, null);
        }
        keyCloakService.deleteKeyCloakAccount(account.getMsisdn());
        accountTransactionService.deleteAccount(request, account);
        return true;
    }

    @Override
    @SneakyThrows
    public boolean add(AddAccountReqDTO request, HttpServletRequest httpServletRequest) {
        // Validate before insert
        this.validateBeforeAddNewAccount(request);
        // Save account and log
        accountTransactionService.add(request);

        String defaultSmsMessage = NoticeTemplate.IMPORT_USER_NOTIFY_SMS_DEFAULT_MESSAGE;
        String smsMessage = applicationSettingCommonService.getMessageByKey(NoticeTemplate.SEND_NOTIFICATION_USER_ADDED, null, perRequestContextDto.getBearToken(), defaultSmsMessage);
        accountExcelService.sendSmsNotifyAfterAddNewUser(request.getRequestId(), request.getPhone(), request.getPassword(), smsMessage);
        return true;
    }

    private void validateBeforeAddNewAccount(AddAccountReqDTO request) {
        String requestId = request.getRequestId();
        accountService.validateMsisdnBeforeAddNew(request.getPhone(), requestId);
        accountService.validateEmailBeforeAddNew(request.getEmail(), requestId);
        accountService.validateUnitIdBeforeAddNew(request.getUnitId(), requestId);
        this.validateRoleBeforeAdd(request.getRoleIdList(), requestId);
    }

    private void validateRoleBeforeAdd(final List<String> roleIds, final String requestId) {
        if (CollectionUtils.isEmpty(roleIds))
            return;
        roleIds.forEach(roleId -> {
            if (roleRepository.findById(roleId).isEmpty()) {
                logger.error("Unit id was not found");
                throw new BusinessEx(requestId, AccountErrorCode.Role.ROLE_DOES_NOT_EXISTS, null);
            }
        });
    }

    @Override
    @SneakyThrows
    public boolean approvalAccount(AdminVerifiedAccountReqDTO request, HttpServletRequest httpServletRequest) {
        Account account = accountRepository.findById(request.getId()).orElseThrow(() -> {
            logger.error("--> Account {} was not found", request.getId());
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.DOES_NOT_EXISTS, null);
        });
        if (!account.getStatus().equals(Status.PENDING)){
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ACCOUNT_APPROVED, null);
        }

        Account accountLogin = accountRepository.findByAccountId(perRequestContextDto.getCurrentAccountId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_FOUND, null);
        });

        List<RoleType> roles = roleRepository.findRoleByAccountId(perRequestContextDto.getCurrentAccountId())
                .stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
        if (roles.contains(RoleType.DIRECTOR) && !roles.contains(RoleType.ADMIN) && !accountLogin.getUnitId().equals(account.getUnitId())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_AUTHORIZED, null);
        }

        String action = "";
        String msisdnOfRegister = account.getMsisdn();
        if (Status.INACTIVE.equals(Status.valueOf(request.getStatus()))) {
            account.setStatus(Status.INACTIVE);
            action = "rejected";
            accountRepository.save(account);
        } else if (Status.ACTIVE.equals(Status.valueOf(request.getStatus()))) {
            String fullName = "";
            Optional<AccountInfo> accountInfoOptional = accountInfoRepository.findByAccountId(account.getId());
            if (accountInfoOptional.isPresent()) {
                fullName = accountInfoOptional.get().getFullName();
            }
            ActionLog actionLog = actionLogRepository.findFirstByAccountIdAndIsVerifiedOtpAndActionTypeOrderByLastUpdatedAtDesc(account.getId(), true, ActionType.PRE_REGISTER_FAMILY);
            if (actionLog == null) {
                logger.error("Cannot find any action log create account");
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Keycloak.CREATE_FAIL, null);
            }
            UserRegisterReqDTO userRegisterReqDTO = objectMapper.readValue(actionLog.getData(), UserRegisterReqDTO.class);
            String password = passwordUtils.decrypt(userRegisterReqDTO.getPassword());
            try (Response response = keyCloakService.createKeycloakUser(account.getMsisdn(), account.getEmail(), fullName, password)) {
                if (response.getStatus() != 201) {
                    throw new BusinessEx(request.getRequestId(), AccountErrorCode.Keycloak.CREATE_FAIL, null);
                }
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Keycloak.CREATE_FAIL, null);
            }
            String newKeycloakUserId = keyCloakService.getKeycloakUserIdByUsername(account.getMsisdn());
            account.setKeycloakID(newKeycloakUserId);
            account.setStatus(Status.ACTIVE);
            account.setApproverId(perRequestContextDto.getCurrentAccountId());

//          remove hash password after approval -> update to action_log
            userRegisterReqDTO.setPassword(null);
            userRegisterReqDTO.setConfirmPassword(null);
            actionLog.setData(objectMapper.writeValueAsString(userRegisterReqDTO));

            action = "approved";
            actionLogRepository.save(actionLog);
            accountRepository.save(account);
        }
        sendApprovalSmsNotifyToRegister(request.getRequestId(), msisdnOfRegister, action);
        return true;
    }

    private void sendApprovalSmsNotifyToRegister(String requestId, String registerMsisdn, String action) {
        String defaultMessage = NoticeTemplate.ADMIN_APPROVAL_SMS_DEFAULT_MESSAGE;
        String message = applicationSettingCommonService.getMessageByKey(NoticeTemplate.SEND_NOTIFICATION_TO_REGISTER, requestId, perRequestContextDto.getBearToken(), defaultMessage).replace("{action}", action);
        super.sendSMS(registerMsisdn, message);
    }

    @Override
    public ProfileDetailResDTO getDetailProfile(BaseRequest request, HttpServletRequest httpServletRequest) {
        String accountId = perRequestContextDto.getCurrentAccountId();
        ProfileDetailResDTO response = accountRepository.getDetailProfile(accountId).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.WRONG_INFO, null);
        });
        
        AccountSettingDTO accountSetting = accountSettingService.getAccountSetting(accountId);
        response.setCarRegistrationActive(accountSetting.getCarRegistrationActive());
        if (!StringUtils.isEmpty(response.getCanteenId())) {
            CheckCanteenForChefResDTO resDTO = resourceRestTemplate.checkCanteenForChef(response.getCanteenId(),null, perRequestContextDto.getBearToken());
            response.setCanteenName(resDTO.getCanteenName());
        }
        List<FileAttach> fileAttach = fileService.findImageByObjectIdAndType(accountId, ObjectType.ACCOUNT);
        List<FileResponse> listResponse = mapper.map(fileAttach, new TypeToken<List<FileResponse>>() {
        }.getType());

        response.setAvatar(listResponse);
        List<Role> rolesOfAccount = roleRepository.findRoleByAccountId(accountId);
        if (CollectionUtils.isNotEmpty(rolesOfAccount)) {
            rolesOfAccount.forEach(role -> role.setFunctions(functionRepository.findFunctionsUsingRoleId(role.getId())));
            List<LoginRoleResDTO> responseRoles = new ArrayList<>();
            for (Role role : rolesOfAccount) {
                LoginRoleResDTO loginRoleResDTO = LoginRoleResDTO.mapper(role);
                responseRoles.add(loginRoleResDTO);
            }
            response.setRoles(responseRoles);
        }
        return response;
    }

    @Override
    @SneakyThrows
    public boolean updateProfile(ProfileEditReqDTO request, MultipartFile files, HttpServletRequest httpServletRequest) {
        String id = perRequestContextDto.getCurrentAccountId();
        Account account = accountRepository.findAccountIdAndStatus(id, Status.ACTIVE).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.WRONG_INFO, null);
        });
        if (accountRepository.findFirstByEmail(request.getEmail()) != null) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.EMAIL_EXISTED, null);
        }
        AccountInfo accountInfo = accountInfoRepository.findByAccountId(id).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_FOUND, null);
        });

        UpdateKeycloakInfoReqDTO updateKeycloakInfoReqDTO = new UpdateKeycloakInfoReqDTO();
        boolean isNeedToUpdateKeycloakInfo = false;

        if (StringUtils.isNotBlank(request.getName()) && !StringUtils.equals(accountInfo.getFullName(), request.getName())) {
            accountInfo.setFullName(request.getName());
            updateKeycloakInfoReqDTO.setFullName(request.getName());
            isNeedToUpdateKeycloakInfo = true;
        }

        if (StringUtils.isNotBlank(request.getDriverLicense())) {
            accountInfo.setDriverLicense(request.getDriverLicense());
        }

        if (StringUtils.isNotBlank(request.getEmail()) && !StringUtils.equals(account.getEmail(), request.getEmail())) {
            account.setEmail(request.getEmail());
            updateKeycloakInfoReqDTO.setEmail(request.getEmail());
            isNeedToUpdateKeycloakInfo = true;
        }

        if (isNeedToUpdateKeycloakInfo) {
            keyCloakService.updateKeycloakInfo(updateKeycloakInfoReqDTO, account);
        }
        accountTransactionService.updateProfile(request, files, id, account, accountInfo);
        return true;
    }

    @Override
    public List<AccountDeviceResDTO> getInfoDeviceAccountByRole(AccountDeviceReqDTO request, HttpServletRequest httpServletRequest) {
        Account account = accountRepository.findById(request.getAccountId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_FOUND, null);
        });
        if (StringUtils.isNotBlank(request.getRoleId()) || StringUtils.isNotBlank(request.getRoleCode())) {
            Role role = roleRepository.findFirstByIdOrCode(request.getRoleId(), Objects.isNull(request.getRoleCode()) ? null : RoleType.valueOf(request.getRoleCode())).orElseThrow(() -> {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Role.NOT_FOUND, null);
            });
            String unitId = account.getUnitId();
            if (!role.getCode().equals(RoleType.DIRECTOR)) {
                unitId = null;
            }
            return accountRepository.getInfoDeviceAccountByRole(request.getAccountId(), unitId, role.getId());
        } else {
            return accountRepository.getInfoDeviceAccount(account.getId());
        }
    }

    @Override
    public Page<AccountFilterResDTO> filter(AccountFilterReqDTO request, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Status status = null;
        if (StringUtils.isNotBlank(request.getStatus())) {
            status = Status.valueOf(request.getStatus());
        }
        Page<AccountFilterResDTO> res = accountRepository.getList(
                request.getName(),
                request.getEmail(),
                request.getMsisdn(),
                request.getCorrectMsisdn(),
                StringUtils.isBlank(request.getUnitId()) ? null : request.getUnitId(),
                status,
                pageable);
        logger.info("Found:{} acc.", res.getTotalElements());
        if (res.getContent().isEmpty()) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Role.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
        }
        return new PageImpl<>(res.getContent(), pageable, res.getTotalElements());
    }

    @Override
    public List<DriverFilterResDTO> getListDriver(AccountFilterReqDTO request, HttpServletRequest httpServletRequest) {
        Status status = null;
        if (StringUtils.isNotBlank(request.getStatus())) {
            status = Status.valueOf(request.getStatus());
        }
        return accountRepository.getListDriver(
                request.getName(),
                request.getEmail(),
                request.getMsisdn(),
                status);
    }

    @Override
    public List<DriverFilterResDTO> getListDriverAssign(DriverAssignReqDTO request, HttpServletRequest httpServletRequest) {
        if (!request.getIds().isEmpty()) {
            return accountRepository.getListDriverForAssign(request.getIds());
        } else {
            return accountRepository.getListDriver(null, null, null, Status.ACTIVE);
        }
    }

    @Override
    public AccountInfoPermissionResDTO getInfoPerUser(SimpleRequest request, HttpServletRequest httpServletRequest) {
        AccountInfoPermissionResDTO res = accountRepository.getDetailAccInfoPer(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ACCOUNT_ID_NOT_FOUND, null);
        });

        List<PermissionAccountDTO> lstRole = roleRepository.getListAllPer();
        List<PermissionAccountDTO> lstRolePerAcc = roleRepository.getListPerAcc(request.getId());

        for (PermissionAccountDTO role : lstRolePerAcc) {
            if (lstRole.contains(role)) {
                lstRole.get(lstRole.indexOf(role)).setChecked(true);
            }
        }
        res.setLstPer(lstRole);

        return res;
    }

    @Override
    public List<AccountReportBookingCarDTO> accountReportBookingCars(AccountIdsDTO accountIdDTO, HttpServletRequest httpServletRequest) {
        List<String> ids = accountIdDTO.getAccountId();
        List<AccountReportBookingCarDTO> accountReportBookingCarDTOS = new ArrayList<>();
        for (String id : ids) {
            AccountReportBookingCarDTO accountReportBookingCarDTO = accountRepository.listAccount(id);
            if (Objects.nonNull(accountReportBookingCarDTO)) {
                List<FileAttach> avatars = fileService.findImageByObjectIdAndType(accountReportBookingCarDTO.getAccountId(), ObjectType.ACCOUNT);
                if (CollectionUtils.isNotEmpty(avatars)) {
                    accountReportBookingCarDTO.setAvtUrl(avatars.get(0).getUrl());
                }
                accountReportBookingCarDTOS.add(accountReportBookingCarDTO);
            }
        }
        return accountReportBookingCarDTOS;
    }

    @Override
    public List<AccountReportBookingCarDTO> accountReports(AccountMsisdnDTO accountMsisdnDTO, HttpServletRequest httpServletRequest) {
        return accountRepository.accountReports(accountMsisdnDTO.getMsisdn());
    }

    @Override
    public List<AccountDeviceResDTO> getInfoDevicesByAccountIds(AccountsInfoRequest request, HttpServletRequest httpServletRequest) {
        List<AccountDeviceResDTO> list = new ArrayList<>();
        List<String> accountIds = request.getAccountIds();
        if (!accountIds.isEmpty()) {
            for (String id : accountIds) {
                List<AccountDeviceResDTO> devices = getDeviceByAccountId(id, request.getRequestId());
                list.addAll(devices);
            }
        }
        return list;
    }

    @Override
    public List<AccountDetailResDTO> getInfoAccountInfoByAccountIds(AccountsInfoRequest request, HttpServletRequest httpServletRequest) {
        List<AccountDetailResDTO> list = new ArrayList<>();
        List<String> accountIds = request.getAccountIds();
        if (!accountIds.isEmpty()) {
            list = accountRepository.getListAccountsByIds(accountIds);
        }
        return list;
    }

    private List<AccountDeviceResDTO> getDeviceByAccountId(String accountId, String requestId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> {
            throw new BusinessEx(requestId, AccountErrorCode.ACCOUNT.NOT_FOUND, null);
        });
        return accountRepository.getInfoDeviceAccount(account.getId());
    }
}
