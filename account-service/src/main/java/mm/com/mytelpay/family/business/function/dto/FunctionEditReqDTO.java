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
public class FunctionEditReqDTO extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 100)
    private String id;

    @SizeRegex(max = 50)
    @NotBlank
    private String code;

    @SizeRegex(max = 100)
    @NotBlank
    private String name;

    private List<String> endPoints;

    @SizeRegex(max = 100)
    private String parentId;

}
