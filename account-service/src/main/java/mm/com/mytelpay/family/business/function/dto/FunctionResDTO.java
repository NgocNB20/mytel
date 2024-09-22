package mm.com.mytelpay.family.business.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionResDTO {

    private String permissionId;

    private String code;

    private String name;

    private Status status;

}
