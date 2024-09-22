package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CanteenResourceDTO {

    private String id;

    private String name;

    private String unitId;

    private String address;

    private Integer seats;

    private Status status;
}
