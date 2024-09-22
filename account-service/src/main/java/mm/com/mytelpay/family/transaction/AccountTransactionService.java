package mm.com.mytelpay.family.transaction;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.account.NewAccountBuilder;
import mm.com.mytelpay.family.business.account.dto.AddAccountReqDTO;
import mm.com.mytelpay.family.business.account.dto.UpdateAccountReqDTO;
import mm.com.mytelpay.family.business.account.dto.UserRegisterReqDTO;
import mm.com.mytelpay.family.business.account.dto.keycloak.UpdateKeycloakInfoReqDTO;
import mm.com.mytelpay.family.business.account.dto.profile.ProfileEditReqDTO;
import mm.com.mytelpay.family.business.account.service.AccountExcelService;
import mm.com.mytelpay.family.business.balance.dto.UpdateBalanceDTO;
import mm.com.mytelpay.family.business.notification.ApplicationSettingCommonService;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.CheckCanteenForChefReqDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.CheckCanteenForChefResDTO;
import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.ActionLog;
import mm.com.mytelpay.family.model.entities.*;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repo.AccountBalanceHistoryRepository;
import mm.com.mytelpay.family.utils.PasswordUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import java.util.*;
import mm.com.mytelpay.family.business.account.dto.profile.CarRegistrationReqDTO;

@Slf4j
@Service
public class AccountTransactionService extends AccountBaseBusiness {

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private PerRequestContextDto perRequestContextDto;

    @Autowired
    private AccountExcelService accountExcelService;

    @Autowired
    private AccountBalanceHistoryRepository accountBalanceHistoryRepository;

    @Autowired
    public ApplicationSettingCommonService applicationSettingCommonService;

    @Autowired
    public ResourceRestTemplate resourceRestTemplate;
    @Value("${role.chef}")
    private RoleType roleChef;
    @SneakyThrows
    @Transactional
    public void add(AddAccountReqDTO request) {
        String accountId = createNewAccountAndAssignInfo(request);
        String data = objectMapper.writeValueAsString(request);
        insertActionLog(request.getRequestId(), accountId, ActionType.ADD_ACCOUNT, null, request.getPhone(), data);
    }

    @SneakyThrows
    @Transactional
    public void updateAccount(UpdateAccountReqDTO request, Account account) {
        if(account.getStatus().equals(Status.INACTIVE) && Status.INACTIVE.equals(Status.valueOf(request.getStatus()))) {
            return;
        }
        handleUpdateAccountDetails(request, account);
        String data = objectMapper.writeValueAsString(request);
        insertActionLog(request.getRequestId(), account.getId(), ActionType.EDIT_PROFILE, null, request.getPhone(), data);
    }

    @SneakyThrows
    @Transactional
    public void updateProfile(ProfileEditReqDTO request, MultipartFile files, String id, Account account, AccountInfo accountInfo){
        if (Objects.nonNull(files)) {
            deleteFile(id, ObjectType.ACCOUNT);
            createSingleFile(files, accountInfo.getAccountId(), ObjectType.ACCOUNT);
        }
        accountInfoRepository.save(accountInfo);
        accountRepository.save(account);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.EDIT_PROFILE, objectMapper.writeValueAsString(request));
    }

    @SneakyThrows
    @Transactional
    public void deleteAccount(SimpleRequest request, Account account){
        accountRepository.delete(account);
        List<AccountRole> accountRole = accountRoleRepository.findByAccountId(request.getId());
        if(!accountRole.isEmpty()){
            accountRoleRepository.deleteAll(accountRole);
        }
        Optional<AccountInfo> accountInfo = accountInfoRepository.findByAccountId(request.getId());
        accountInfo.ifPresent(info -> accountInfoRepository.delete(info));
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.DELETE_USER, objectMapper.writeValueAsString(request));
    }

    public String createNewAccountAndAssignInfo(AddAccountReqDTO request) {
        Account newAccount = NewAccountBuilder.newAccountBuilder(request);
        String accountId = newAccount.getId();
        AccountInfo newAccountInfo = NewAccountBuilder.newAccountInfoBuilder(request, accountId);
        List<AccountRole> accountRoles = createAccountRoles(request.getRoleIdList(), accountId, request.getCanteenId());
        try (Response response = keyCloakService.createKeycloakUser(request.getPhone(), request.getEmail(), request.getName(), request.getPassword())) {
            if (response.getStatus() != 201) {
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Keycloak.CREATE_FAIL, null);
            }
            String newKeycloakUserId = keyCloakService.getKeycloakUserIdByUsername(request.getPhone());
            newAccount.setKeycloakID(newKeycloakUserId);
            newAccount.setApproverId(perRequestContextDto.getCurrentAccountId());
            accountRepository.save(newAccount);
            accountInfoRepository.save(newAccountInfo);
            accountRoleRepository.saveAll(accountRoles);
        }
        return accountId;
    }

    private List<AccountRole> createAccountRoles(List<String> roleIds, String accountId, String canteenId) {
        List<AccountRole> accountRoles = new ArrayList<>();
        if (CollectionUtils.isEmpty(roleIds)) {
            roleRepository.findRoleIdUsingRoleCode(RoleType.END_USER).ifPresent(roleId -> {
                AccountRole newAccountRole = new AccountRole(accountId, roleId);
                accountRoles.add(newAccountRole);
            });
            return accountRoles;
        }
        Role role = roleRepository.findByCode(roleChef);
        String roleIdChef = role.getId();
        Set<String> setRoleIds = new HashSet<>(roleIds);
        for (String roleId : setRoleIds) {
            AccountRole accountRole = new AccountRole(accountId, roleId);
            if (roleId.equals(roleIdChef)) {
                CheckCanteenForChefReqDTO reqDTO = new CheckCanteenForChefReqDTO();
                if (StringUtils.isEmpty(canteenId)) {
                    throw new BusinessEx(null, AccountErrorCode.Canteen.CANTEEN_NOT_FOUND, null);
                }
                reqDTO.setCanteenId(canteenId);
                CheckCanteenForChefResDTO resDTO = resourceRestTemplate.checkCanteenForChef(reqDTO.getCanteenId(), reqDTO.getRequestId(), perRequestContextDto.getBearToken());
                if (ObjectUtils.isEmpty(resDTO)) {
                    throw new BusinessEx(null, AccountErrorCode.Canteen.CANTEEN_NOT_FOUND, null);
                }
            }
            accountRoles.add(accountRole);
        }
        return accountRoles;
    }

    @SneakyThrows
    private void handleUpdateAccountDetails(UpdateAccountReqDTO request, Account account){
        AccountInfo accountInfo = accountInfoRepository.findFirstByAccountId(account.getId());
        handleUpdateAccountInfo(request, accountInfo, account);
        handleUpdateAccountRole(request.getRoleIdList(), account.getId(), request.getCanteenId());
        if(Status.INACTIVE.equals(account.getStatus()) && Status.ACTIVE.equals(Status.valueOf(request.getStatus())) && StringUtils.isEmpty(account.getKeycloakID())) {
            ActionLog actionLog = actionLogRepository.findFirstByAccountIdAndIsVerifiedOtpAndActionTypeOrderByLastUpdatedAtDesc(account.getId(), true, ActionType.UPDATE_ACCOUNT);
            if (actionLog == null) {
                log.error("Cannot find any action log create account");
                throw new BusinessEx(request.getRequestId(), AccountErrorCode.Keycloak.CREATE_FAIL, null);
            }
            UserRegisterReqDTO userRegisterReqDTO = objectMapper.readValue(actionLog.getData(), UserRegisterReqDTO.class);
            String password = passwordUtils.decrypt(userRegisterReqDTO.getPassword());
            keyCloakService.createKeycloakUser(account.getMsisdn(), account.getEmail(), request.getName(), password);
            String newKeycloakUserId = keyCloakService.getKeycloakUserIdByUsername(account.getMsisdn());
            account.setKeycloakID(newKeycloakUserId);
        }
        if (!Status.valueOf(request.getStatus()).equals(account.getStatus())) {
            account.setStatus(Status.valueOf(request.getStatus()));
            if (Status.INACTIVE.equals(Status.valueOf(request.getStatus()))) {
                keyCloakService.logoutByKeycloakId(account.getKeycloakID());
            }
        }
        if (!StringUtils.equals(request.getUnitId(), account.getUnitId())) {
            account.setUnitId(request.getUnitId());
        }
        account.setCanteenId(request.getCanteenId());
        accountRepository.save(account);
    }

    private void handleUpdateAccountInfo(UpdateAccountReqDTO request, AccountInfo accountInfo, Account account) {
        UpdateKeycloakInfoReqDTO updateKeycloakInfoReqDTO = new UpdateKeycloakInfoReqDTO();
        boolean isNeedToUpdateKeycloakInfo = false;

        if (StringUtils.isNotBlank(request.getName()) && !StringUtils.equals(accountInfo.getFullName(), request.getName())) {
            accountInfo.setFullName(request.getName());
            updateKeycloakInfoReqDTO.setFullName(request.getName());
            isNeedToUpdateKeycloakInfo = true;
            accountInfoRepository.save(accountInfo);
        }


        if (StringUtils.isNotBlank(request.getEmail()) && !StringUtils.equals(account.getEmail(), request.getEmail())) {
            account.setEmail(request.getEmail());
            updateKeycloakInfoReqDTO.setEmail(request.getEmail());
            isNeedToUpdateKeycloakInfo = true;
            accountRepository.save(account);
        }

        if (isNeedToUpdateKeycloakInfo) {
            keyCloakService.updateKeycloakInfo(updateKeycloakInfoReqDTO, account);
        }

    }

    private void handleUpdateAccountRole(List<String> inputRoleIds, String accountId, String canteenId) {
        if (CollectionUtils.isEmpty(inputRoleIds)) {
            Role endUserRole = roleRepository.findByCode(RoleType.END_USER);
            String endUserRoleId = endUserRole == null ? "" : endUserRole.getId();
            accountRoleRepository.deleteByAccountIdExceptEndUserRole(accountId, endUserRoleId);
        }
        else {
//            List<String> accountRoleIds = accountRoleRepository.findByAccountId(accountId).stream().map(AccountRole::getRoleId).collect(Collectors.toList());
//            if(this.areTwoRoleIdListsEqual(inputRoleIds, accountRoleIds)) {
//                return;
//            }
            List<AccountRole> accountRoles = new ArrayList<>();
            Role role = roleRepository.findByCode(roleChef);
            String roleIdChef = role.getId();
            for (String roleId : inputRoleIds) {
                if (roleId.equals(roleIdChef)) {
                    CheckCanteenForChefReqDTO reqDTO = new CheckCanteenForChefReqDTO();
                    if (StringUtils.isEmpty(canteenId)) {
                        throw new BusinessEx(null, AccountErrorCode.Canteen.CANTEEN_NOT_FOUND, null);
                    }
                    reqDTO.setCanteenId(canteenId);
                    CheckCanteenForChefResDTO resDTO = resourceRestTemplate.checkCanteenForChef(reqDTO.getCanteenId(), reqDTO.getRequestId(), perRequestContextDto.getBearToken());
                    if (ObjectUtils.isEmpty(resDTO)) {
                        throw new BusinessEx(null, AccountErrorCode.Canteen.CANTEEN_NOT_FOUND, null);
                    }
                }
                AccountRole newAccountRole = new AccountRole(accountId, roleId);
                accountRoles.add(newAccountRole);
            }
            accountRoleRepository.deleteByAccountId(accountId);
            accountRoleRepository.saveAll(accountRoles);
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

        List<?> copy = new ArrayList<>(roleIdsOfUser);

        for (Object element : roleIdsInput) {
            if (!copy.remove(element)) {
                return false;
            }
        }

        return copy.isEmpty();
    }

    @Transactional
    public void saveImportedAccountsToDB(List<Account> accounts, List<AccountInfo> accountInfos, List<AccountRole> accountRoles){
        accountRepository.saveAll(accounts);
        accountRoleRepository.saveAll(accountRoles);
        accountInfoRepository.saveAll(accountInfos);
    }

    @SneakyThrows
    @Transactional
    public void updateBalance(UpdateBalanceDTO request) {
        String receiverId = request.getReceiverId();
        String createdBy = request.getCreatedBy();
        AccountInfo accountInfo = accountInfoRepository.findByAccountIdAndLock(receiverId).orElseThrow(() -> new BusinessEx(AccountErrorCode.ACCOUNT.NOT_FOUND, null));
        int currentBalance = accountInfo.getCurrentBalance();
        int amount = request.getAmount();
        if (!canUpdateBalance(currentBalance, amount)) {
            throw new BusinessEx(AccountErrorCode.ACCOUNT.BALANCE_NOT_ENOUGH, null);
        }

        AccountBalanceHistory accountBalanceHistory = new AccountBalanceHistory(null, createdBy, receiverId, request.getReceiverPhone(), request.getAmount(), accountInfo.getCurrentBalance(), request.getActionType());
        accountInfo.setCurrentBalance(accountInfo.getCurrentBalance() + request.getAmount());
        insertActionLog(request.getRequestId(), createdBy, ActionType.UPDATE_BALANCE, objectMapper.writeValueAsString(request));

        accountBalanceHistoryRepository.save(accountBalanceHistory);
        accountInfoRepository.save(accountInfo);
    }

    private boolean canUpdateBalance(int currentBalance, int amount) {
        return currentBalance + amount >= 0;
    }
    
    @SneakyThrows
    @Transactional
    public void updateCarRegistration(CarRegistrationReqDTO request, String id, Account account){
        accountRepository.save(account);
        insertActionLog(request.getRequestId(), perRequestContextDto.getCurrentAccountId(), ActionType.EDIT_PROFILE, objectMapper.writeValueAsString(request));
    }
}
