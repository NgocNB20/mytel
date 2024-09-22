package mm.com.mytelpay.family.business.meal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.enums.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealFilterResDTO {
    private String id;

    private String name;

    private String day;

    private LocalDateTime createdAt;
    private String canteenName;

    private Double price;

    private MealType type;

    private Status status;

}
