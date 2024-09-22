package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NotBlank;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class AccountDeviceResDTO {

    private String deviceId;

    @NotBlank
    private String accountId;

    private String lang;

}