package mm.com.mytelpay.family.business.meal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDetailDto {
    private Long id;
    private String fileName;
    private String url;
}
