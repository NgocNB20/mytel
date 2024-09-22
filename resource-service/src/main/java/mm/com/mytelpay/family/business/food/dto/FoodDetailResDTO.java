package mm.com.mytelpay.family.business.food.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.enums.FoodType;
import mm.com.mytelpay.family.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodDetailResDTO {

    private String id;

    private String name;

    private FoodType type;

    private Status status;

    private String createdBy;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    private List<FileResponse> files;
}
