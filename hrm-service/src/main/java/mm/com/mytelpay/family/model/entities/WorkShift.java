package mm.com.mytelpay.family.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkShift {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    private String name;
    private LocalTime inTime;
    private LocalTime outTime;
    private LocalTime breakStart;
    private LocalTime breakEnd;
    private String workHours; // hoặc Duration nếu cần tính toán thời gian

    private int lateMinTime;
    private int lateMaxTime;
    private int lateWarnTime;
    private int lateHalfDay;

    private int earlyMinTime;
    private int earlyMaxTime;
    private int earlyWarnTime;


}
