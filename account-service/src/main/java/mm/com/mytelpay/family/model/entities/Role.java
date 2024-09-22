package mm.com.mytelpay.family.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.models.BaseModel;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROLE")
@SuperBuilder(toBuilder = true)
public class Role extends BaseModel {

    @Id
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "CODE", columnDefinition = "VARCHAR(100)")
    @Enumerated(EnumType.STRING)
    private RoleType code;

    @Column(name = "NAME", columnDefinition = "VARCHAR(100)")
    private String name;

    @Transient
    @JsonProperty
    private List<String> functionCodes;

    @Transient
    @JsonIgnore
    private List<Function> functions;

}
