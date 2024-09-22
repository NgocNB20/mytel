package mm.com.mytelpay.family.business.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.notification.dto.*;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDeviceResDTO;
import mm.com.mytelpay.family.enums.Payload;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.PageImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface NoticeService {

    NotificationDetailDTO readNotice(SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean delete(NotificationDeleteReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    PageImpl<NotificationFilterResDTO> filter(NotificationFilterReqDTO reqDTO, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean sendNotice(SendNoticeReqDTO sendNoticeReqDTO, HttpServletRequest httpServletRequest);

    void sendNotification(String requestId, String value, String valueDefault, AccountDTO account, List<AccountDeviceResDTO> accountDeviceResDTOS, Payload type, String bookingId);
}