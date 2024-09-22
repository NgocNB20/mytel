package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.utils.Util;

@Data
public class ReportFeeOrderMealResDTO {

    private String id;

    private String userId;

    private String userName;

    private String phone;

    private String canteenId;

    private String canteenName;

    private String unitId;

    private String unit;

    private Integer fee;

    private Integer refund;

    private Integer totalFee;

    private MealDetailStatus status;

    public ReportFeeOrderMealResDTO(String id, String userId,
                                    String canteenId, String unitId,
                                    Integer fee, MealDetailStatus status) {
        this.id = id;
        this.userId = userId;
        this.canteenId = canteenId;
        this.unitId = unitId;
        this.fee = fee;
        this.status = status;
    }

    public String getPhone(){
        phone = Util.refineMobileNumber(phone);
        return phone;
    }
}
