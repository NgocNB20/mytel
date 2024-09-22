package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.utils.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReportOrderMealResDTO {

    private String id;

    private String userId;

    private String userName;

    private String phone;

    private MealType mealType;

    private LocalDateTime orderTime;

    private LocalDate mealTime;

    private String canteenId;

    private String canteenName;

    private String unitId;

    private String unit;

    private MealDetailStatus status;


    public ReportOrderMealResDTO(String id, String userId, MealType mealType, LocalDateTime orderTime, LocalDate mealTime,
                                 String canteenId, String unitId, MealDetailStatus status) {
        this.id = id;
        this.userId = userId;
        this.mealType = mealType;
        this.orderTime = orderTime;
        this.mealTime = mealTime;
        this.canteenId = canteenId;
        this.unitId = unitId;
        this.status = status;
    }

    public String getPhone(){
        phone = Util.refineMobileNumber(phone);
        return phone;
    }
}
