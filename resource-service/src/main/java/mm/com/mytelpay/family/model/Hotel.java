package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "HOTEL")
@Data
public class Hotel extends BaseModelResource {

    @Column(name = "PHONE", columnDefinition = "VARCHAR(100)")
    private String phone;

    @Column(name = "ROLES_ALLOW", columnDefinition = "VARCHAR(255)")
    private String rolesAllow;

    @Column(name = "RATING", columnDefinition = "NUMERIC(1)")
    private Integer rating;

    @Column(name = "DISTRICT_ID", columnDefinition = "VARCHAR(36)")
    private String districtId;

    @Column(name = "PROVINCE_ID", columnDefinition = "VARCHAR(36)")
    private String provinceId;

    @Column(name = "MAX_PRICE")
    private Integer maxPrice;

    @Column(name = "MAX_PLUS_PRICE")
    private Integer maxPlusPrice;

}
