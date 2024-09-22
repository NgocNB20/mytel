package mm.com.mytelpay.family.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseModelResource extends BaseModel {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "CODE", columnDefinition = "VARCHAR(50)")
    private String code;

    @Column(name = "NAME", columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(name = "ADDRESS", columnDefinition = "VARCHAR(255)")
    private String address;

    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(255)")
    private String description;
}
