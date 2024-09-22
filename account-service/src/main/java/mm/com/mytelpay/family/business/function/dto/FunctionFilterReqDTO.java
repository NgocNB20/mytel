package mm.com.mytelpay.family.business.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.utils.BasePagination;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionFilterReqDTO extends BasePagination {

    private String code;

    private String name;

    private String parentId;

}
