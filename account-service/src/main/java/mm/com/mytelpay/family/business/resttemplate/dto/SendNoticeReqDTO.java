package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.Data;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO;
import mm.com.mytelpay.family.enums.Payload;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;

@Data
public class SendNoticeReqDTO extends BaseRequest {

    @EnumRegex(enumClass = Payload.class)
    private Payload payload;

    private String payloadId;

    private String value;

    private List<AccountDeviceResDTO> accountRecipient;

    public SendNoticeReqDTO(Payload payload, String payloadId, String value, List<AccountDeviceResDTO> accountRecipient) {
        this.payload = payload;
        this.payloadId = payloadId;
        this.value = value;
        this.accountRecipient = accountRecipient;
    }

}
