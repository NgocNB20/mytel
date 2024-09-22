package mm.com.mytelpay.family.business.rolehasfunction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionPerRole {
    private String functionId;
    private String functionCode;
    private String functionName;
    private boolean checked;
    private String parentId;

}
