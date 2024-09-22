package mm.com.mytelpay.family.business.unit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.model.entities.Unit;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnitImportResultResDTO {
    private Unit unit;

    private String res;
}
