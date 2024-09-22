package mm.com.mytelpay.family.business.meal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealResDTO {
    private String name;
    private String canteenId;

    private LocalDateTime day;

    private Double price;

    private MealType type;

    private Status status;


}
