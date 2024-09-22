package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDTO {
    private String id;

    private String name;

    private String code;

    private String createdBy;
}
