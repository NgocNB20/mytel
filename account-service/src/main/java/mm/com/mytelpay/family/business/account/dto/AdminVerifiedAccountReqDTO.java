package mm.com.mytelpay.family.business.account.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminVerifiedAccountReqDTO extends BaseRequest {

    @NotBlank
    private String id;

    @EnumRegex(enumClass = Status.class)
    @NotBlank
    private String status;

}
