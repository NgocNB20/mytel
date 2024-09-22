package mm.com.mytelpay.family.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkGroupDTO {
    @JsonIgnore
    private String id;
    private String name;
    private int numberOfPeople;
    private String type;
    private String location;
    private String locationType;

    public WorkGroupDTO(String id, String type, String location) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.locationType = checkTime(); // Ghép giá trị tại đây
    }

    public void setType(String type) {
        this.type = type;
        this.locationType = checkTime();
    }

    public void setLocation(String location) {
        this.location = location;
        this.locationType = checkTime();
    }

    private String checkTime() {
        return this.type.equals(this.location) ? "yes" : "no";
    }
}
