package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealResourceDTO {
    private String id;

    private String name;

    private String canteenName;

    private LocalDateTime day;

    private Double price;

    private MealType type;

    private MealDetailStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    private String createdBy;
}
