package mm.com.mytelpay.family.business.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionAddReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 50)
    private String code;

    @NotBlank
    @SizeRegex(max = 100)
    private String name;

    private List<String> endPoints;

    @SizeRegex(max = 100)
    private String parentId;

}
