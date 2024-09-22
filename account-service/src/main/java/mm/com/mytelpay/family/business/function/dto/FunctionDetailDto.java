package mm.com.mytelpay.family.business.function.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FunctionDetailDto {
    private String id;

    private String code;

    private String name;

    private String parentId;

    private List<String> endPoints;
}
