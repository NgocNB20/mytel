package mm.com.mytelpay.family.filter;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Data
public class PerRequestContextDto {

    private String bearToken;

    private String currentAccountId;

}
