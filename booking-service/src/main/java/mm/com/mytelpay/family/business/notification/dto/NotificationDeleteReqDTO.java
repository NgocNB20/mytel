package mm.com.mytelpay.family.business.notification.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.DeleteType;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;

@Data
public class NotificationDeleteReqDTO extends BaseRequest {

    private String id;

    @NotBlank
    @EnumRegex(enumClass = DeleteType.class)
    private String type;
}
