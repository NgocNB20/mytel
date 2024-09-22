package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "BOOKING_MEAL_DETAIL")
@Data
public class BookingMealDetail extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "BOOKING_MEAL_ID", columnDefinition = "VARCHAR(36)")
    private String bookingMealId;

    @Column(name = "MEAL_ID", columnDefinition = "VARCHAR(36)")
    private String mealId;

    @Column(name = "MEAL_DAY", columnDefinition = "DATE")
    private LocalDate mealDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", columnDefinition = "VARCHAR(50)")
    private MealDetailStatus status;

    @Column(name = "REASON", columnDefinition = "VARCHAR(255)")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", columnDefinition = "VARCHAR(50)")
    private MealType type;

    @Column(name = "FEE", columnDefinition = "NUMERIC")
    private Integer fee;

}
