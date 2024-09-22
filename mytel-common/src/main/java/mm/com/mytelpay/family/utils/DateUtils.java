package mm.com.mytelpay.family.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtils {

    private DateUtils() {
    }

    public static boolean isValidTimeFromAndTimeTo(LocalDateTime timeFrom, LocalDateTime timeTo) {
        return !timeFrom.isAfter(timeTo);
    }

    public static boolean isNowAfterTimeOfMealDay(LocalDate mealDate, LocalTime timeOfDay) {
        LocalDateTime mealDateTime = mealDate.atTime(timeOfDay);
        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.isAfter(mealDateTime);
    }

}
