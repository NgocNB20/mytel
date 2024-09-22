package mm.com.mytelpay.family.business.notification.dto;

import lombok.Data;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
public class NotificationFilterReqDTO extends BasePagination {

    private Boolean isRead;
}
