package mm.com.mytelpay.family.model.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`FUNCTION`")
@SuperBuilder(toBuilder = true)
public class Function extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "CODE", unique = true, columnDefinition = "VARCHAR(50)")
    private String code;

    @Column(name = "NAME", columnDefinition = "VARCHAR(100)")
    private String name;

    @Column(name = "ENDPOINTS", columnDefinition = "VARCHAR(2500)")
    private String endPoints;

    @Column(name = "PARENT_ID", columnDefinition = "VARCHAR(100)")
    private String parentId;

}
