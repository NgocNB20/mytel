package mm.com.mytelpay.family.business.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.model.Menu;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuImportResultResDTO {
    private Menu menu;

    private String res;
}
