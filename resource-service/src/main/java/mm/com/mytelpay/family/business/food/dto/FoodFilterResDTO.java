package mm.com.mytelpay.family.business.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.enums.FoodType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodFilterResDTO {

    private String id;

    private String name;

    private FoodType type;

    private LocalDateTime createdAt;

    private List<FileResponse> files;

    public FoodFilterResDTO(String id, String name, FoodType type, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
    }
}
