package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.ObjectType;
import mm.com.mytelpay.family.enums.Status;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "FILE_ATTACH")
@Data
public class FileAttach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", columnDefinition = "INTEGER")
    private Long id;

    @Column(name = "FILE_NAME", columnDefinition = "VARCHAR(255)")
    private String fileName;

    @Column(name = "URL", columnDefinition = "VARCHAR(255)")
    private String url;

    @Column(name = "OBJECT_ID", columnDefinition = "VARCHAR(255)")
    private String objectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "OBJECT_TYPE", columnDefinition = "VARCHAR(36)")
    private ObjectType objectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", columnDefinition = "VARCHAR(36)")
    private Status status = Status.ACTIVE;
}
