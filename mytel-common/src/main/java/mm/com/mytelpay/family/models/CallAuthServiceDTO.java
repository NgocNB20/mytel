package mm.com.mytelpay.family.models;

import lombok.Data;

import java.util.Set;

@Data
public class CallAuthServiceDTO {

    private Set<CustomAuthority> customAuthorities;

    private String accountId;

    private String msisdn;

}
