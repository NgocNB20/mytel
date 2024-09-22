package mm.com.mytelpay.family.business.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDetailResDTO {

    private String mealId;

    private String name;

    private String foodIds;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    private String createdBy;

}
