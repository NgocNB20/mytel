package mm.com.mytelpay.family.business.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.FoodType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodFilterResDto {
    private String id;
    private String name;
    private FoodType type;
    private LocalDateTime createdAt;
    private Set<FileFilterResDto> files;
}
