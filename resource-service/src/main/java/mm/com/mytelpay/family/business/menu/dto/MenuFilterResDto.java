package mm.com.mytelpay.family.business.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.Day;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuFilterResDto implements Comparable<MenuFilterResDto> {
    private Day day;
    private String name;
    private List<MealFilterResDto> menu;

    @Override
    public int compareTo(@NotNull MenuFilterResDto o) {
        return this.getDay().compareTo(o.getDay());
    }
}
