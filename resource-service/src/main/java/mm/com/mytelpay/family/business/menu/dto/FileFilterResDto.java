package mm.com.mytelpay.family.business.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileFilterResDto {
    private Long id;
    private String fileName;
    private String url;
}

