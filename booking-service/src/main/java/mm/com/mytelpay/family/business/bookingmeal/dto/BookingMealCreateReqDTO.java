package mm.com.mytelpay.family.business.bookingmeal.dto;

import lombok.Data;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.validate.DateFilterRegex;
import mm.com.mytelpay.family.exception.validate.EnumRegex;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.models.dto.BaseRequest;

import java.util.List;


@Data
public class BookingMealCreateReqDTO extends BaseRequest {

    @NotBlank
    @DateFilterRegex
    private String from;

    @NotBlank
    @DateFilterRegex
    private String to;

    @NotBlank
    private String canteenId;

    @NotBlank
    @EnumRegex(enumClass = MealType.class)
    private List<String> mealType;

    private String note;

    @EnumRegex(enumClass = Day.class)
    private List<String> weekdays;
}