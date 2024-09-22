package mm.com.mytelpay.family.business.function.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FunctionFilterResDTO {
    private String id;

    private String code;

    private String name;

    private String endPoints;

    private String parentId;

    private String parentName;

}
