package mm.com.mytelpay.family.business.notification.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.Payload;

import java.time.LocalDateTime;

@Data
public class NotificationFilterResDTO {

    private String id;

    private Payload payload;

    private String idPayload;

    private DataNotificationDTO data;

    private Integer isRead;

    private LocalDateTime createdAt;
}
