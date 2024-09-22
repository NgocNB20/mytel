package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FunctionResDTO {

    private String functionId;

    private String parentId;

    private String code;

    private String name;

}
