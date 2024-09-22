package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccInfoBasicDTO {

    private String accountId;

    private String email;

    private String name;

    private String msisdn;

}
