package mm.com.mytelpay.family.business.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.notification.dto.*;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDeviceResDTO;
import mm.com.mytelpay.family.config.firebase.Notice;
import mm.com.mytelpay.family.enums.DeleteType;
import mm.com.mytelpay.family.enums.Payload;
import mm.com.mytelpay.family.enums.Replace;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.model.NotificationHistory;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repo.NotificationHistoryRepository;
import mm.com.mytelpay.family.utils.NoticeTemplate;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Service
public class NoticeServiceImpl extends BookingBaseBusiness implements NoticeService {

    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    @Autowired
    private NoticeTransaction noticeTransaction;

    @Override
    public NotificationDetailDTO readNotice(SimpleRequest request, HttpServletRequest httpServletRequest) {
        String accountId = perRequestContextDto.getCurrentAccountId();
        accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), perRequestContextDto.getBearToken());

        NotificationHistory notificationHistory = notificationHistoryRepository.findFirstByIdAndAccountId(request.getId(), accountId).orElseThrow(()->{
            throw new BusinessEx(request.getRequestId(), BookingErrorCode.Notification.NOT_FOUND, BookingErrorCode.Notification.NOT_FOUND);
        });
        String lang = Util.getLanguage(httpServletRequest);
        String bsJson = notificationHistory.getData();
        DataNotificationDTO dataNotificationDTO = new DataNotificationDTO();
        if (bsJson != null && !bsJson.trim().isEmpty()) {
            JSONObject exploreObject = new JSONObject(bsJson);
            String bsName = exploreObject.getJSONObject(lang).toString();
            dataNotificationDTO = new Gson().fromJson(bsName, DataNotificationDTO.class);
        }

        notificationHistory.setRead(true);
        notificationHistoryRepository.save(notificationHistory);

        return new NotificationDetailDTO(notificationHistory, dataNotificationDTO);
    }

    @Override
    public boolean delete(NotificationDeleteReqDTO request, HttpServletRequest httpServletRequest) {
        String accountId = perRequestContextDto.getCurrentAccountId();
        accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), perRequestContextDto.getBearToken());
        if (request.getType().equals(DeleteType.RECORD.toString())){
            if (StringUtils.isBlank(request.getId())){
                throw new RequestEx(request.getRequestId(), CommonException.Request.INPUT_IS_REQUIRED, "parameter.required.id");
            }
            NotificationHistory notificationHistory = notificationHistoryRepository.findFirstByIdAndAccountId(request.getId(), accountId).orElseThrow(()->{
                throw new BusinessEx(request.getRequestId(), BookingErrorCode.Notification.NOT_FOUND, BookingErrorCode.Notification.NOT_FOUND);
            });
            notificationHistoryRepository.deleteById(notificationHistory.getId());
        }else if (request.getType().equals(DeleteType.ALL.toString())){
            notificationHistoryRepository.deleteByAccountId(accountId);
        }
        return true;
    }

    @Override
    public PageImpl<NotificationFilterResDTO> filter(NotificationFilterReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String accountId = perRequestContextDto.getCurrentAccountId();

        accountRestTemplate.getAccountInfo(accountId, request.getRequestId(), perRequestContextDto.getBearToken());

        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        Page<NotificationHistory> page = notificationHistoryRepository.filterNotification(accountId,
                request.getIsRead(),
                pageable);
        String lang = Util.getLanguage(httpServletRequest);

        Gson gson = new Gson();
        List<NotificationFilterResDTO> filterResDTO = page.getContent().stream().map(p -> {
            NotificationFilterResDTO noticeFilterResDTO = mapper.map(p, NotificationFilterResDTO.class);
            String bsJson = p.getData();
            if (bsJson != null && !bsJson.trim().isEmpty()) {
                JSONObject exploreObject = new JSONObject(bsJson);
                String bsName = exploreObject.getJSONObject(lang).toString();
                noticeFilterResDTO.setData(gson.fromJson(bsName, DataNotificationDTO.class));
            }
            noticeFilterResDTO.setIsRead(p.isRead() ? 1 : 0 );
            return noticeFilterResDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(filterResDTO, pageable, page.getTotalElements());
    }

    @Override
    public boolean sendNotice(SendNoticeReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        try{
            String accountId = perRequestContextDto.getCurrentAccountId();
            String bearAuth = perRequestContextDto.getBearToken();
            AccountDTO accountDTO = new AccountDTO();
            if (StringUtils.isNotEmpty(accountId)){
                accountDTO = accountRestTemplate.getAccountInfo(accountId, reqDTO.getRequestId(), bearAuth);
            }
            String value = reqDTO.getValue();
            this.sendNotification(reqDTO.getRequestId(), value, null, accountDTO, reqDTO.getAccountRecipient(), Payload.valueOf(reqDTO.getPayload()), reqDTO.getPayloadId());
            return true;
        } catch (Exception e){
            logger.error("Send notice fail: ", e);
            return false;
        }
    }

    public void sendNotification(String requestId, String value, String valueDefault, AccountDTO accountDTO, List<AccountDeviceResDTO> accountDeviceResDTOS, Payload type, String bookingId){
        try {
            if (Objects.isNull(accountDeviceResDTOS) || accountDeviceResDTOS.isEmpty()){
                return;
            }
            Map<String, Object> subs = new HashMap<>();
            if (Objects.nonNull(accountDTO)) {
                subs.put(Replace.USERNAME.getValue(), accountDTO.getName());
            }
            if (StringUtils.isBlank(value)) value = valueDefault;
            value = (Util.substitute(subs, value));

            Notice notice = new Notice();
            Map<String, String> myMap = new HashMap<>();
            myMap.put(NoticeTemplate.PAY_LOAD, String.valueOf(type));
            myMap.put(NoticeTemplate.ID_PAYLOAD,bookingId);
            notice.setData(myMap);

            Map<String, List<String>> objectsByLang = createMapDeviceId(accountDeviceResDTOS);

            for (Map.Entry<String, List<String>> entry : objectsByLang.entrySet()) {
                String lang = entry.getKey();
                List<String> deviceTokens = entry.getValue();

                if (StringUtils.isNotEmpty(value) && StringUtils.isNotBlank(lang)) {
                    value = sendNoticeAndSetValue(value, valueDefault, subs, notice, lang, deviceTokens);
                }
            }
            noticeTransaction.saveNotice(accountDeviceResDTOS, value, type, bookingId);
        } catch (Exception e) {
            logger.error("Send notice fail :", e);
        }
    }

    private String sendNoticeAndSetValue(String value, String valueDefault, Map<String, Object> subs, Notice notice, String lang, List<String> deviceTokens) {
        JSONObject exploreObject;
        try {
            exploreObject = new JSONObject(value);
        }catch (Exception e){
            value = Util.substitute(subs, valueDefault);
            exploreObject = new JSONObject(value);
        }
        String data;
        try {
            data = exploreObject.getJSONObject(lang).toString();
        } catch (JSONException e) {
            value = Util.substitute(subs, valueDefault);
            exploreObject = new JSONObject(value);
            data = exploreObject.getJSONObject(lang).toString();
        }
        DataNotificationDTO dataResDTO = new Gson().fromJson(data, DataNotificationDTO.class);
        notice.setRegistrationTokens(deviceTokens);
        notice.setSubject(dataResDTO.getSubject());
        notice.setContent(dataResDTO.getContent());
        notificationService.sendNotification(notice);
        logger.info("send notification success");
        return value;
    }

    @NotNull
    private Map<String, List<String>> createMapDeviceId(List<AccountDeviceResDTO> accountDeviceResDTOS) {
        Map<String, List<String>> objectsByLang = new HashMap<>();
        for (AccountDeviceResDTO obj : accountDeviceResDTOS) {
            String lang = obj.getLang();
            if (StringUtils.isEmpty(obj.getDeviceId()) || StringUtils.isEmpty(obj.getLang())){
                continue;
            }
            objectsByLang.computeIfAbsent(lang, key -> new ArrayList<>())
                    .stream()
                    .filter(deviceId -> deviceId.equals(obj.getDeviceId()))
                    .findAny()
                    .ifPresent(deviceId -> {});
            objectsByLang.get(lang).add(obj.getDeviceId());
        }
        return objectsByLang;
    }
}
