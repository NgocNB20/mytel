package mm.com.mytelpay.family.business.notification.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.Payload;
import mm.com.mytelpay.family.model.NotificationHistory;

import java.time.LocalDateTime;

@Data
public class NotificationDetailDTO {

    private String idPayload;

    private Payload payload;

    private String subject;

    private String content;

    private LocalDateTime createdAt;

    public NotificationDetailDTO(NotificationHistory history, DataNotificationDTO dataNotificationDTO) {
        this.payload = history.getPayload();
        this.idPayload = history.getIdPayload();
        this.createdAt = history.getCreatedAt();
        this.subject = dataNotificationDTO.getSubject();
        this.content = dataNotificationDTO.getContent();
    }
}
