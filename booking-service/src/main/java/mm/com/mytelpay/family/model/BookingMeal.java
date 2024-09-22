package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "BOOKING_MEAL")
@Data
public class BookingMeal extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "ACCOUNT_ID", columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Column(name = "CANTEEN_ID", columnDefinition = "VARCHAR(36)")
    private String canteenId;

    @Column(name = "FROM_TIME", columnDefinition = "DATE")
    private LocalDate fromTime;

    @Column(name = "TO_TIME", columnDefinition = "DATE")
    private LocalDate toTime;

    @Column(name = "NOTE", columnDefinition = "VARCHAR(255)")
    private String note;

    @Column(name = "UNIT_ID", columnDefinition = "VARCHAR(36)")
    private String unitId;
}
