package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.Status;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "CANTEEN")
@Data
public class Canteen  extends BaseModelResource {

    @Column(name = "UNIT_ID", columnDefinition = "VARCHAR(36)")
    private String unitId;

    @Column(name = "SEATS", columnDefinition = "NUMERIC")
    private Integer seats;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", columnDefinition = "VARCHAR(36)")
    private Status status;

}
