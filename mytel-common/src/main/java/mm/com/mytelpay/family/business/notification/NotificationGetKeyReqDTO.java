package mm.com.mytelpay.family.business.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationGetKeyReqDTO extends BaseRequest {

    @NotBlank
    private String key;

    public NotificationGetKeyReqDTO(String key, String requestId) {
        this.setKey(key);
        this.setRequestId(requestId);
    }
}
