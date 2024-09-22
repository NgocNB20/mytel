package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.models.BaseModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "PROVINCE")
@Data
public class Province  extends BaseModel {
    @Id
    @Column(name = "ID" , unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @NotNull
    @Column(name = "NAME" , columnDefinition = "VARCHAR(50)")
    private String name;
    @Column(name = "DESCRIPTION" , columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "CODE", columnDefinition = "VARCHAR(50)")
    private String code;
}
