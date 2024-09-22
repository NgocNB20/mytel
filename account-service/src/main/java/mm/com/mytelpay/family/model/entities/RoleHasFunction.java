package mm.com.mytelpay.family.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ROLE_HAS_FUNCTION")
public class RoleHasFunction {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ROLE_ID", columnDefinition = "VARCHAR(36)")
    private String roleId;

    @Column(name = "FUNCTION_ID", columnDefinition = "VARCHAR(36)")
    private String functionId;

}
