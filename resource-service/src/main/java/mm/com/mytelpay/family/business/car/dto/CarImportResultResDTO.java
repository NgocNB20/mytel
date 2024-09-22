package mm.com.mytelpay.family.business.car.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarImportResultResDTO {
    private CarCreateReqDTO car;

    private String res;
}
