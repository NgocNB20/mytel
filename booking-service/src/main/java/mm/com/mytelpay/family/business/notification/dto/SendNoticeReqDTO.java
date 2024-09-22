package mm.com.mytelpay.family.business.notification.dto;

import lombok.Data;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDeviceResDTO;
import mm.com.mytelpay.family.enums.Payload;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import javax.validation.Valid;
import java.util.List;

@Data
public class SendNoticeReqDTO extends BaseRequest {

    @NotBlank
    @EnumRegex(enumClass = Payload.class)
    private String payload;

    @NotBlank
    private String payloadId;

    @NotBlank
    private String value;

    @Valid
    private List<AccountDeviceResDTO> accountRecipient;

}
