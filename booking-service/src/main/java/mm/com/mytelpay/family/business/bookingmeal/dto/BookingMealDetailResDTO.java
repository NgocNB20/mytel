package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.GetMealByIdDTO;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.model.BookingMealDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingMealDetailResDTO {
    private String id;

    private AccountDTO account;

    private MealType mealType;

    private LocalDate mealDay;

    private String canteenName;

    private String canteenAddress;

    private String reason;

    private MealDetailStatus status ;

    private Integer fee;

    private LocalDateTime createdAt;

    private GetMealByIdDTO meal;

    public BookingMealDetailResDTO(BookingMealDetail bookingMealDetail) {
        this.id = bookingMealDetail.getId();
        this.mealType = bookingMealDetail.getType();
        this.mealDay = bookingMealDetail.getMealDay();
        this.status = bookingMealDetail.getStatus();
        this.createdAt = bookingMealDetail.getCreatedAt();
    }
}
