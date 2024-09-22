package mm.com.mytelpay.family.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.models.BaseModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "MEAL")
@Data
public class Meal extends BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ID", unique = true, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "NAME", columnDefinition = "VARCHAR(255)")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "DAY", columnDefinition = "VARCHAR(36)")
    private Day day;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", columnDefinition = "VARCHAR(36)")
    private MealType type;

    @Column(name = "CANTEEN_ID", columnDefinition = "VARCHAR(36)")
    private String canteenId;

    @Column(name = "PRICE", columnDefinition = "NUMERIC(20)")
    private Integer price;

    @Column(name = "CREATED_BY", columnDefinition = "VARCHAR(255)")
    private String createdBy;

}
