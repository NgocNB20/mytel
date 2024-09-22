package mm.com.mytelpay.family.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "UNIT")
@Data
public class Unit extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "NAME", columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(name = "CODE", columnDefinition = "VARCHAR(255)")
    private String code;

    @Column(name = "CREATED_BY", columnDefinition = "VARCHAR(255)")
    private String createdBy;

}
