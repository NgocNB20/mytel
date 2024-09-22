package mm.com.mytelpay.family.model.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ShiftGroup {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "SHIFT_ID", columnDefinition = "VARCHAR(36)")
    private String shiftId;

    @Column(name = "GROUP_ID", columnDefinition = "VARCHAR(36)")
    private String groupId;
}
