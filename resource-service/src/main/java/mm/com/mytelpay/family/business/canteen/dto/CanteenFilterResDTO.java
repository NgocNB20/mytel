package mm.com.mytelpay.family.business.canteen.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CanteenFilterResDTO {
    private String id;
    private String code;
    private String name;
    private String unitId;
    private String Unit;
    private Integer numberSeats;
    private String address;
    private String description;

    public CanteenFilterResDTO(String id, String code, String name, String unitId, Integer numberSeats, String address, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.unitId = unitId;
        this.numberSeats = numberSeats;
        this.address = address;
        this.description = description;
    }
}
