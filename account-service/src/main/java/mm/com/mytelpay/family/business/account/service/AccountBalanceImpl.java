package mm.com.mytelpay.family.business.account.service;

import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceReqDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO;
import mm.com.mytelpay.family.business.balance.dto.*;
import mm.com.mytelpay.family.business.notification.ApplicationSettingCommonService;
import mm.com.mytelpay.family.business.resttemplate.dto.SendNoticeReqDTO;
import mm.com.mytelpay.family.enums.*;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.model.entities.AccountInfo;
import mm.com.mytelpay.family.model.entities.Role;
import mm.com.mytelpay.family.repo.AccountBalanceHistoryRepository;
import mm.com.mytelpay.family.transaction.AccountTransactionService;
import mm.com.mytelpay.family.utils.DateUtils;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class AccountBalanceImpl extends AccountBaseBusiness implements AccountBalanceService {

    @Autowired
    private PerRequestContextDto perRequestContextDto;
    @Autowired
    private AccountTransactionService accountTransactionService;
    @Autowired
    private AccountBalanceHistoryRepository accountBalanceHistoryRepository;
    @Autowired
    public ApplicationSettingCommonService applicationSettingCommonService;
    @Autowired
    public AccountManageService accountManageService;

    @Value("${whitelist.phone.approved.chef}")
    private String whitelistPhoneApproved;

    @Override
    public boolean topupBalance(TopupReqDTO request, HttpServletRequest httpServletRequest) {

        // Validate chef in while list allow update Balance
        if (!validateWhitelistChef(whitelistPhoneApproved, Util.getMsisdnFromJwt(perRequestContextDto.getBearToken()))) {
            logger.error("[Topup Balance Error] Chef not in whitelist allow update Balance");
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NO_PERMISSION_CHEF_APPROVAL, null);
        }

        Account receiver = accountRepository.findFirstByMsisdn(request.getPhone());
        if (receiver == null) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.PHONE_NOT_EXISTS, null);
        } else if (!receiver.getStatus().equals(Status.ACTIVE)) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.NOT_ALLOW_UPDATE, null);
        }

        boolean isReceiverChef = roleRepository.findRoleByAccountId(receiver.getId()).stream().map(Role::getCode).anyMatch(RoleType.CHEF::equals);
        if (isReceiverChef) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.CANNOT_TOPUP_FOR_CHEF, null);
        }
        UpdateBalanceDTO updateBalanceDTO = new UpdateBalanceDTO(receiver.getId(), request.getPhone(), Integer.parseInt(request.getAmount()), BalanceActionType.TOPUP, perRequestContextDto.getCurrentAccountId());
        accountTransactionService.updateBalance(updateBalanceDTO);

        AccountInfo chefInfo = accountInfoRepository.findFirstByAccountId(perRequestContextDto.getCurrentAccountId());
        sendTopupNotiToReceiver(receiver.getId(), request.getRequestId(), request.getAmount(), chefInfo.getFullName(), httpServletRequest);

        return true;
    }

    private boolean validateWhitelistChef(String whitelistPhone, String phone) {
        if (!whitelistPhone.isEmpty() && !phone.isEmpty()) {
            List<String> whitelistPhoneList = Arrays.asList(whitelistPhone.split(","));
            if (whitelistPhoneList.contains(phone)) {
                return true;
            }
        }
        return false;
    }

    private void sendTopupNotiToReceiver(String receiverId, String requestId, String amount, String chefName, HttpServletRequest httpServletRequest) {
        String message = getTopupNoticeMessage(amount, chefName, requestId);
        AccountDeviceReqDTO accountDeviceReqDTO = new AccountDeviceReqDTO(receiverId, null, null);
        List<AccountDeviceResDTO> deviceOfReceivers = accountManageService.getInfoDeviceAccountByRole(accountDeviceReqDTO, httpServletRequest);
        SendNoticeReqDTO sendNoticeReqDTO = new SendNoticeReqDTO(Payload.TOPUP, receiverId, message, deviceOfReceivers);
        bookingRestTemplate.sendFcmNoti(sendNoticeReqDTO, perRequestContextDto.getBearToken());
    }

    private String getTopupNoticeMessage(String amount, String chefName, String requestId) {
        String defaultMessage = NoticeTemplate.SEND_NOTIFICATION_TOPUP_BALANCE_DEFAULT_VALUE;
        Map<String, Object> subs = new HashMap<>();
        subs.put(Replace.NO_OF_POINT.getValue(), amount);
        subs.put(Replace.CHEF_NAME.getValue(), chefName);
        String message = applicationSettingCommonService.getMessageByKey(NoticeTemplate.SEND_NOTIFICATION_TOPUP_BALANCE, requestId, perRequestContextDto.getBearToken(), defaultMessage);
        message = (Util.substitute(subs, message));
        return message;
    }

    @Override
    public boolean updateBalance(UpdateBalanceReqDTO request, HttpServletRequest httpServletRequest) {
        /*// Validate chef in while list allow update Balance
        if (!validateWhitelistChef(whitelistPhoneApproved, Util.getMsisdnFromJwt(perRequestContextDto.getBearToken()))) {
            logger.error("[Topup Balance Error] Chef not in whitelist allow update Balance");
            throw new BusinessEx(AccountErrorCode.ACCOUNT.NO_PERMISSION_CHEF_APPROVAL, null);
        }*/

        String currentUserId = perRequestContextDto.getCurrentAccountId();
        boolean isReceiverChef = roleRepository.findRoleByAccountId(currentUserId).stream().map(Role::getCode).anyMatch(RoleType.CHEF::equals);
        if (isReceiverChef) {
            throw new BusinessEx(AccountErrorCode.ACCOUNT.CANNOT_TOPUP_FOR_CHEF, null);
        }
        Optional<Account> accountOptional = accountRepository.findById(currentUserId);
        String receiverPhone = accountOptional.map(Account::getMsisdn).orElse("");
        int amount = Integer.parseInt(request.getAmount());
        BalanceActionType actionType = BalanceActionType.valueOf(request.getActionType());
        if (actionType == BalanceActionType.ORDER_MEAL) {
            amount = -Math.abs(amount);
        }
        UpdateBalanceDTO updateBalanceDTO = new UpdateBalanceDTO(currentUserId, receiverPhone, amount, BalanceActionType.valueOf(request.getActionType()), currentUserId);
        accountTransactionService.updateBalance(updateBalanceDTO);
        return true;
    }

    @Override
    public Page<GetBalanceHistoriesResDTO> getBalanceHistories(GetBalanceHistoriesReqDTO request, List<BalanceActionType> actionTypeList, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        LocalDateTime fromTime = StringUtils.isBlank(request.getFrom()) ? null : Util.convertToLocalDateTime(request.getFrom());
        LocalDateTime toTime = StringUtils.isBlank(request.getTo()) ? null : Util.convertToLocalDateTime(request.getTo());
        if (fromTime != null && toTime !=null && !DateUtils.isValidTimeFromAndTimeTo(fromTime, toTime)) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.TIME_FROM_TIME_TO_INVALID, null);
        }
        return accountBalanceHistoryRepository.getBalanceHistory(fromTime, toTime, perRequestContextDto.getCurrentAccountId(), actionTypeList, pageable);
    }

    @Override
    public Page<GetBalanceHistoriesResDTO> getTopupHistories(GetBalanceHistoriesReqDTO request, List<BalanceActionType> actionTypeList, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        LocalDateTime fromTime = StringUtils.isBlank(request.getFrom()) ? null : Util.convertToLocalDateTime(request.getFrom());
        LocalDateTime toTime = StringUtils.isBlank(request.getTo()) ? null : Util.convertToLocalDateTime(request.getTo());
        if (fromTime != null && toTime !=null && !DateUtils.isValidTimeFromAndTimeTo(fromTime, toTime)) {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.TIME_FROM_TIME_TO_INVALID, null);
        }
        return accountBalanceHistoryRepository.getTopupHistory(fromTime, toTime, perRequestContextDto.getCurrentAccountId(), actionTypeList, pageable);
    }

}
