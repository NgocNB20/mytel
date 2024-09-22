package mm.com.mytelpay.family.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomAuthority implements GrantedAuthority {
    private String authority;
}
