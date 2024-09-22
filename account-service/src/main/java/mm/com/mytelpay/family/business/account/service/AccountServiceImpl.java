package mm.com.mytelpay.family.business.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.account.NewAccountBuilder;
import mm.com.mytelpay.family.business.account.dto.*;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO;
import mm.com.mytelpay.family.business.account.dto.login.LoginReqDTO;
import mm.com.mytelpay.family.business.account.dto.login.LoginResDTO;
import mm.com.mytelpay.family.business.account.dto.RefreshTokenReqDTO;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.business.notification.ApplicationSettingCommonService;
import mm.com.mytelpay.family.business.resttemplate.dto.DataNotificationDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.SendNoticeReqDTO;
import mm.com.mytelpay.family.enums.*;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.ActionLog;
import mm.com.mytelpay.family.model.entities.*;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.repo.DeviceRepository;
import mm.com.mytelpay.family.repo.UnitRepository;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import mm.com.mytelpay.family.utils.PasswordUtils;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Service
public class AccountServiceImpl extends AccountBaseBusiness implements AccountService {

    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private PasswordUtils passwordUtils;
    @Autowired
    private ApplicationSettingCommonService applicationSettingCommonService;
    @Autowired
    private PerRequestContextDto perRequestContextDto;

    @Override
    public LoginResDTO login(LoginReqDTO request, HttpServletRequest servletRequest) {
        Account account = accountRepository.findFirstByMsisdn(request.getMsisdn());
        if (account == null) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.INCORRECT_PHONE_NUMBER_OR_PASS, null);
        }
        if (Status.LOCK.equals(account.getStatus())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.THE_ACCOUNT_HAS_BEEN_LOCKED, null);
        }
        if (Status.PENDING.equals(account.getStatus())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ACCOUNT_HAS_NOT_BEEN_APPROVED, null);
        }
        try {
            Keycloak keycloak = keyCloakService.newKeycloakBuilderWithPasswordCredentials(request.getMsisdn(),
                    request.getPassword()).build();
            AccessTokenResponse accessTokenResponse = keycloak.tokenManager().getAccessToken();
            String deviceLanguage = servletRequest.getHeader("Accept-Language");
            handleDeviceWhenLogin(account.getId(), request.getDeviceId(), request.getOs(), deviceLanguage);
            return this.genLoginResponse(account, accessTokenResponse);
        } catch (NotAuthorizedException e) {
            logger.error("--> Wrong msisdn {} or pin ", request.getMsisdn());
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.INCORRECT_PHONE_NUMBER_OR_PASS, null);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.Business.LOGIN_FAIL, null);
        }
    }

    private void handleDeviceWhenLogin(String accountId, String deviceId, String osType, String deviceLanguage) {
        Device device = deviceRepository.findFirstByDeviceId(deviceId);
        if (device != null) {
            device.setLastUpdatedAt(LocalDateTime.now());
            device.setAccountId(accountId);
            device.setLang(deviceLanguage);
        }
        else {
            device = new Device(deviceId, accountId, OsType.valueOf(osType), deviceLanguage);
        }
        deviceRepository.save(device);
    }

    private LoginResDTO genLoginResponse(Account account, AccessTokenResponse accessTokenResponse) {
        LoginResDTO loginResDTO = new LoginResDTO();
        loginResDTO.setAccessTokenResponse(accessTokenResponse);
        AccountInfo accountInfo = accountInfoRepository.findFirstByAccountId(account.getId());
        Unit unit = unitRepository.findFirstById(account.getUnitId());
        List<Role> rolesOfAccount = roleRepository.findRoleByAccountId(account.getId());
        if (CollectionUtils.isNotEmpty(rolesOfAccount)) {
            rolesOfAccount.forEach(role -> role.setFunctions(functionRepository.findFunctionsUsingRoleId(role.getId())));
        }
        List<FileAttach> fileAttach = fileService.findImageByObjectIdAndType(account.getId(), ObjectType.ACCOUNT);
        FileResponse avatarResponse = new FileResponse();
        if (CollectionUtils.isNotEmpty(fileAttach)) {
            avatarResponse = mapper.map(fileAttach.get(0), new TypeToken<FileResponse>() {
            }.getType());
        }
        return LoginResDTO.mapper(accessTokenResponse, account, accountInfo, rolesOfAccount, unit, avatarResponse);
    }

    @Override
    public ResponseEntity<CommonResponseDTO> refreshToken(RefreshTokenReqDTO request) {
        return keyCloakService.refreshToken(request.getRefreshToken(), request.getRequestId());
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public String preRegisterAccount(UserRegisterReqDTO request) {
        try {
            this.validateNewAccount(request);
            String hashPassword = passwordUtils.encrypt(request.getPassword());
            request.setPassword(hashPassword);
            request.setConfirmPassword(hashPassword);
            String data = objectMapper.writeValueAsString(request);
            ActionLog actionlog = super.insertActionLog(request.getRequestId(), null, ActionType.PRE_REGISTER_FAMILY, false, request.getMsisdn(), data);
            String actionLogId = actionlog.getId();
            generateOTP(request.getMsisdn(), actionlog.getId(), null, false);
            return actionLogId;
        } catch (BusinessEx businessEx) {
            throw businessEx;
        }
        catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.REGISTER_FAIL, null);
        }
    }

    @Override
    public boolean resendOtp(ResendOtpReqDTO request) {
        try {
            generateOTP(request.getMsisdn(), request.getOriginalRequestId(), null, false);
            return true;
        }
        catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    private void validateNewAccount(UserRegisterReqDTO request) {
        String requestId = request.getRequestId();
        this.validateMsisdnBeforeAddNew(request.getMsisdn(), requestId);
        this.validateEmailBeforeAddNew(request.getEmail(), requestId);
        this.validateMatchPassword(request.getPassword(), request.getConfirmPassword(), requestId);
        this.validateUnitIdBeforeAddNew(request.getUnitId(), requestId);
    }

    @Override
    public void validateMsisdnBeforeAddNew(final String msisdn, final String requestId) {
        if (accountRepository.findFirstByMsisdn(msisdn) != null) {
            logger.error("Phone number is registered");
            throw new BusinessEx(requestId, AccountErrorCode.ACCOUNT.PHONE_EXISTED, null);
        }
    }

    @Override
    public void validateEmailBeforeAddNew(final String email, final String requestId) {
        if (accountRepository.findFirstByEmail(email) != null) {
            logger.error("Email is registered");
            throw new BusinessEx(requestId, AccountErrorCode.ACCOUNT.EMAIL_EXISTED, null);
        }
    }

    @Override
    public void validateMatchPassword(final String password, final String confirmPassword, final String requestId) {
        if (!StringUtils.equals(password, confirmPassword)) {
            logger.error("Password was not matched");
            throw new BusinessEx(requestId, AccountErrorCode.ACCOUNT.PASS_NOT_MATCH, null);
        }
    }

    @Override
    public void validateUnitIdBeforeAddNew(final String unitId, final String requestId) {
        if (StringUtils.isNotEmpty(unitId) && unitRepository.findById(unitId).isEmpty()) {
            logger.error("Unit id was not found");
            throw new BusinessEx(requestId, AccountErrorCode.Unit.NOT_FOUND, null);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean verifyOtpCreateAccount(UserVerifyOtpReqDTO request) {
        try {
            ActionLog actionLog = findActionById(request.getOriginalRequestId());
            String requestId = request.getRequestId();
            this.validateActionLog(actionLog, request.getMsisdn(), requestId, request.getOriginalRequestId());
            verifyOTP(request.getMsisdn(), request.getOtp(),
                    requestId, null, false);
            UserRegisterReqDTO userRegisterReqDTO = objectMapper.readValue(actionLog.getData(), UserRegisterReqDTO.class);
            String accountId = this.createNewAccountAndAssignInfos(userRegisterReqDTO);
            updateActionLog(actionLog, accountId);
            this.sendRegisterSmsNotifyToAdmin(requestId, userRegisterReqDTO.getMsisdn(), userRegisterReqDTO.getUnitId(), accountId);
        }
        catch (BusinessEx businessEx) {
            throw businessEx;
        }
        catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.REGISTER_ACCOUNT_FAIL, null);
        }
        return true;
    }

    private void sendRegisterSmsNotifyToAdmin(String requestId, String registerMsisdn, String unitId, String accountId) {
        String defaultMessage = NoticeTemplate.REGISTER_NOTIFY_SMS_DEFAULT_MESSAGE;
        String message =  applicationSettingCommonService.getMessageByKey(NoticeTemplate.SEND_NOTIFICATION_TO_REGISTER, requestId, perRequestContextDto.getBearToken(), defaultMessage);
        List<Account> adminAccounts = accountRepository.getAccountByRoleCode(RoleType.ADMIN);
        Map<String, Object> subs = new HashMap<>();
        subs.put(Replace.PHONE_NUMBER.getValue(), registerMsisdn);
        String messageReplate =  Util.substitute(subs, message);
        JSONObject exploreObject;
        String data;
        String lang = "en";
        try {
            exploreObject = new JSONObject(messageReplate);
            data = exploreObject.getJSONObject(lang).toString();
        }catch (Exception e){
            messageReplate =  Util.substitute(subs, defaultMessage);
            exploreObject = new JSONObject(messageReplate);
            data = exploreObject.getJSONObject(lang).toString();
        }
        DataNotificationDTO dataResDTO = new Gson().fromJson(data, DataNotificationDTO.class);
        String messageNotice = dataResDTO.getContent();

        if (CollectionUtils.isNotEmpty(adminAccounts)) {
            for (Account account : adminAccounts) {
                super.sendSMS(account.getMsisdn(), messageNotice);
            }
        }

        List<Account> accountDirector = accountRepository.getAccountByRoleCode(RoleType.DIRECTOR);
        List<String> accountIds = adminAccounts.stream().map(Account::getId).collect(Collectors.toList());
        accountIds.addAll(accountDirector.stream().filter(a -> a.getUnitId().equals(unitId)).map(Account::getId).collect(Collectors.toList()));

        List<AccountDeviceResDTO> deviceResDTOS = accountRepository.getInfoDeviceByAccountIds(accountIds);
        SendNoticeReqDTO sendNoticeReqDTO = new SendNoticeReqDTO(Payload.REGISTER_ACCOUNT, accountId, messageReplate, deviceResDTOS);
        bookingRestTemplate.sendFcmNoti(sendNoticeReqDTO, perRequestContextDto.getBearToken());
    }

    private void validateActionLog(ActionLog actionLog, String msisdn, String requestId, String originalRequestId) {
        if (Objects.isNull(actionLog) || !StringUtils.equals(actionLog.getMsisdn(), msisdn)) {
            logger.error("--> Action log create account is not found {}", originalRequestId);
            throw new BusinessEx(requestId, AccountErrorCode.ActionLog.NOT_FOUND, null);
        }
        if (Boolean.TRUE.equals(actionLog.getIsVerifiedOtp())) {
            logger.error("Action log create account is invalid {}", originalRequestId);
            throw new BusinessEx(requestId, "12014", null);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean changePassword(ChangePasswordRequest request) throws JsonProcessingException {
        Account account = accountRepository.findById(perRequestContextDto.getCurrentAccountId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_FOUND, null);
        });
        if (!StringUtils.equals(request.getNewPass(), request.getConfirmNewPass())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.PASS_NOT_MATCH, null);
        }
        if (keyCloakService.validateCurrentPassword(account.getMsisdn(), request.getCurrentPass())) {
            keyCloakService.changeKeycloakPassword(account, request.getNewPass());
            String passwordRequestJson = objectMapper.writeValueAsString(clearPasswordInsertActionLog(request));
            insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(),
                    ActionType.CHANGE_PASSWORD, null, account.getMsisdn(), passwordRequestJson);
        }
        else {
            logger.error("--> Wrong msisdn {} or pin ", account.getMsisdn());
            throw new BusinessEx(request.getRequestId() , AccountErrorCode.ACCOUNT.CURRENT_PASS_WRONG, null);
        }
        return true;
    }

    private ChangePasswordRequest clearPasswordInsertActionLog(ChangePasswordRequest request) {
        request.setNewPass(null);
        request.setConfirmNewPass(null);
        request.setCurrentPass(null);
        return request;
    }

    @Override
    @Transactional
    public boolean logout(LogoutReqDTO logoutRequest) {
        deviceRepository.deleteAllByAccountIdAndDeviceId(logoutRequest.getAccountId(), logoutRequest.getDeviceId());
        keyCloakService.logoutByRefreshToken(logoutRequest.getRefreshToken());
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ActionLogIDDTO forgotPassword(ForgetPasswordRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Account account = accountRepository.findFirstByMsisdn(request.getMsisdn());
        if (account == null) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.PHONE_NOT_EXISTS, null);
        }
        if (Status.LOCK.equals(account.getStatus())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.THE_ACCOUNT_HAS_BEEN_LOCKED, null);
        }
        if (Status.PENDING.equals(account.getStatus())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.ACCOUNT_HAS_NOT_BEEN_APPROVED, null);
        }
        String passwordRequestJson = objectMapper.writeValueAsString(request);
        ActionLog actionLog = insertActionLog(request.getRequestId(), account.getId(), ActionType.FORGOT_PASS, null, request.getMsisdn(), passwordRequestJson);
        generateOTP(request.getMsisdn(), actionLog.getId(), null, false);
        ActionLogIDDTO actionLogIDDTO = new ActionLogIDDTO();
        actionLogIDDTO.setOriginalRequestId(actionLog.getId());
        return actionLogIDDTO;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public boolean verifyOtpResetPassword(UserVerifyOtpReqDTO request) {
        ActionLog actionLog = actionLogRepository.findByIdAndMsisdn(request.getOriginalRequestId(), request.getMsisdn()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ActionLog.NOT_FOUND, null);
        });
        if (Boolean.TRUE.equals(actionLog.getIsVerifiedOtp())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.OTP.OTP_IS_INVALID, null);
        }
        actionLog.setIsVerifiedOtp(true);
        actionLogRepository.save(actionLog);
        return verifyOTP(request.getMsisdn(), request.getOtp(), request.getRequestId(), null, false);
    }

    @Override
    public boolean resetPassword(ResetPasswordRequest request, HttpServletRequest httpServletRequest) {
        if (!StringUtils.equals(request.getNewPass(), request.getConfirmPass())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.PASS_NOT_MATCH, null);
        }
        ActionLog actionLog = actionLogRepository.findByIdAndMsisdn(request.getOriginalRequestId(), request.getMsisdn()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ActionLog.NOT_FOUND, null);
        });
        if(Boolean.FALSE.equals(actionLog.getIsVerifiedOtp())){
            throw new BusinessEx(request.getRequestId() , AccountErrorCode.ActionLog.UNVERIFIED, null);
        }
        if (!checkTimeResetPassword(actionLog.getId())) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ActionLog.TIME_OUT, null);
        }
        Account account = accountRepository.findFirstByMsisdn(request.getMsisdn());
        if (account == null) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_FOUND, null);
        }
        keyCloakService.changeKeycloakPassword(account, request.getNewPass());
        return true;

    }

    @Override
    public boolean lockAccount(String accountId) {
        Account account = accountRepository.findByAccountId(accountId).orElseThrow(() -> new BusinessEx(AccountErrorCode.ACCOUNT.NOT_FOUND, null));
        account.setStatus(Status.LOCK);
        accountRepository.save(account);
        return true;
    }

    public String createNewAccountAndAssignInfos(UserRegisterReqDTO request) {
        Account account = accountRepository.findFirstByEmailOrMsisdn(request.getEmail(), request.getMsisdn());
        if (account != null) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.EXISTED, null);
        }
        Account newAccount = NewAccountBuilder.newAccountBuilder(request);
        AccountInfo newAccountInfo = NewAccountBuilder.newAccountInfoBuilder(request, newAccount.getId());
        Device newDeviceInfo = NewAccountBuilder.newDeviceBuilder(request, newAccount.getId());
        roleRepository.findRoleIdUsingRoleCode(RoleType.END_USER).ifPresent(roleId -> {
            AccountRole newAccountRole = new AccountRole(newAccount.getId(), roleId);
            accountRoleRepository.save(newAccountRole);
        });
        deviceRepository.save(newDeviceInfo);
        accountRepository.save(newAccount);
        accountInfoRepository.save(newAccountInfo);
        return newAccount.getId();
    }

    private boolean checkTimeResetPassword(String originalRequestId) {
        ActionLog actionLog = actionLogRepository.findById(originalRequestId).orElseThrow(() -> {
            throw new BusinessEx(originalRequestId, AccountErrorCode.ActionLog.NOT_FOUND, null);
        });
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime lastUpdatedAt = actionLog.getLastUpdatedAt();
        Duration duration = Duration.between(lastUpdatedAt, currentTime);
        int timeToExpired = 10;
        return duration.toMinutes() <= timeToExpired;
    }

}
