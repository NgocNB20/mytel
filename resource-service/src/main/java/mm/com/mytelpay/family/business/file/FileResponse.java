package mm.com.mytelpay.family.business.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {

    private String id;

    private String fileName;

    private String url;
}
