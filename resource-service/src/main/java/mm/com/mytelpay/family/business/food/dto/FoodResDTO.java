package mm.com.mytelpay.family.business.food.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodResDTO {
    private String name;

    private FoodType type;

    private Status status;

    private String createdBy;

    private String description;

}
