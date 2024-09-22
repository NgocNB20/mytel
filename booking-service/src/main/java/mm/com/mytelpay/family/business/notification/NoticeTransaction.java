package mm.com.mytelpay.family.business.notification;

import mm.com.mytelpay.family.business.resttemplate.dto.AccountDeviceResDTO;
import mm.com.mytelpay.family.enums.Payload;
import mm.com.mytelpay.family.model.NotificationHistory;
import mm.com.mytelpay.family.repo.NotificationHistoryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeTransaction {

    private final Logger logger = LogManager.getLogger(this.getClass().getSimpleName());

    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    @Transactional
    public void saveNotice(List<AccountDeviceResDTO> accountDeviceResDTOS, String data, Payload type, String idPayload) {
        List<String> accountIdReceived = accountDeviceResDTOS.stream()
                .map(AccountDeviceResDTO::getAccountId)
                .distinct()
                .collect(Collectors.toList());
        List<NotificationHistory> historyList = accountIdReceived.stream().map(s -> createNoticeHistory(s, data, type, idPayload)).collect(Collectors.toList());
        notificationHistoryRepository.saveAll(historyList);
        logger.info("insert booking history success");
    }

    private NotificationHistory createNoticeHistory(String accountId, String data, Payload type, String idPayload) {
        NotificationHistory notificationHistory = new NotificationHistory();
        notificationHistory.setAccountId(accountId);
        notificationHistory.setData(data);
        notificationHistory.setPayload(type);
        notificationHistory.setIdPayload(idPayload);
        notificationHistory.setRead(false);
        return notificationHistory;
    }
}
