package mm.com.mytelpay.family.business.bookingmeal;

import mm.com.mytelpay.family.business.notification.NotificationReqDTO;
import mm.com.mytelpay.family.business.resttemplate.ResourceRestTemplate;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.error.BookingEx;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.BookingMealDetail;
import mm.com.mytelpay.family.utils.Constants;
import mm.com.mytelpay.family.utils.DateUtils;
import mm.com.mytelpay.family.utils.Translator;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookingMealValidators {

    private final Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
    private static final String BREAKFAST = "Breakfast";
    private static final String LUNCH = "Lunch";
    private static final String DINNER = "Dinner";

    @Autowired
    private ResourceRestTemplate resourceRestTemplate;
    @Autowired
    public PerRequestContextDto perRequestContextDto;

    public void validateTimeBeforeCancel(List<LocalDate> holidaysInYear, BookingMealDetail bookingMealDetail) {
        MealType mealType = bookingMealDetail.getType();
        LocalDate mealDay = bookingMealDetail.getMealDay();
        MealDetailStatus mealDetailStatus = bookingMealDetail.getStatus();
        if (!MealDetailStatus.PENDING.equals(mealDetailStatus)) {
            throw new BusinessEx(BookingErrorCode.BookingMeal.CANNOT_CANCEL_ORDER_MEAL, null);
        }
        if (isCancelInPublicHoliday(mealDay, holidaysInYear))
            return;
        switch (mealType) {
            case BREAKFAST:
                validateTimeCancelBreakfastBookingMeal(mealDay);
                break;
            case LUNCH:
                validateTimeCancelLunchBookingMeal(mealDay);
                break;
            case DINNER:
                validateTimeCancelDinnerBookingMeal(mealDay);
                break;
            default:
        }
    }

    private boolean isCancelInPublicHoliday(LocalDate mealDay, List<LocalDate> holidaysInYear) {
        return holidaysInYear.contains(mealDay);
    }

    private void validateTimeCancelBreakfastBookingMeal(LocalDate mealDay) {
        if (isCancelBeforeMealDay(mealDay)) {
            return;
        }

        LocalTime deadlineToCancelMeal = this.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_CANCEL_BOOKING_MEAL_KEY, MealType.BREAKFAST, Constants.DEFAULT_DEADLINE_TO_CANCEL_BREAK_FAST_ORDER);
        if (DateUtils.isNowAfterTimeOfMealDay(mealDay, deadlineToCancelMeal)) {
            String message = getErrorMessageForCancel(mealDay, deadlineToCancelMeal);
            throw new BookingEx(BookingErrorCode.BookingMeal.OVER_DUE_DEADLINE_CANCEL_ORDER, message);
        }
    }

    private void validateTimeCancelLunchBookingMeal(LocalDate mealDay) {
        if (isCancelBeforeMealDay(mealDay)) {
            return;
        }

        LocalTime deadlineToCancelMeal = this.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_CANCEL_BOOKING_MEAL_KEY, MealType.LUNCH, Constants.DEFAULT_DEADLINE_TO_CANCEL_LUNCH_ORDER);
        if (DateUtils.isNowAfterTimeOfMealDay(mealDay, deadlineToCancelMeal)) {
            String message = getErrorMessageForCancel(mealDay, deadlineToCancelMeal);
            throw new BookingEx(BookingErrorCode.BookingMeal.OVER_DUE_DEADLINE_CANCEL_ORDER, message);
        }
    }

    private void validateTimeCancelDinnerBookingMeal(LocalDate mealDay) {
        if (isCancelBeforeMealDay(mealDay)) {
            return;
        }

        LocalTime deadlineToCancelMeal = this.getDeadlineTimeByConfigKeyAndMealType(Constants.DEADLINE_TIME_CANCEL_BOOKING_MEAL_KEY, MealType.DINNER, Constants.DEFAULT_DEADLINE_TO_CANCEL_DINNER_ORDER);
        if (DateUtils.isNowAfterTimeOfMealDay(mealDay, deadlineToCancelMeal)) {
            String message = getErrorMessageForCancel(mealDay, deadlineToCancelMeal);
            throw new BookingEx(BookingErrorCode.BookingMeal.OVER_DUE_DEADLINE_CANCEL_ORDER, message);
        }
    }

    private boolean isCancelBeforeMealDay(LocalDate mealDay) {
        return LocalDate.now().isBefore(mealDay);
    }

    public LocalTime getDeadlineTimeByConfigKeyAndMealType(String configKey, MealType mealType, LocalTime defaultValue) {
        LocalTime deadlineTime = defaultValue;
        NotificationReqDTO notificationReqDTO = resourceRestTemplate.getApplicationByKey(configKey, null, perRequestContextDto.getBearToken());
        if (StringUtils.isNotEmpty(notificationReqDTO.getValue())) {
            try {
                JSONObject jsonObj = new JSONObject(notificationReqDTO.getValue());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                Map<String, LocalTime> timeValue = new HashMap<>();

                for (String key : jsonObj.keySet()) {
                    String value = jsonObj.getString(key);
                    LocalTime time = LocalTime.parse(value, formatter);
                    timeValue.put(key, time);
                }
                String mealTypeString = null;
                switch (mealType) {
                    case BREAKFAST:
                        mealTypeString = BREAKFAST;
                        break;
                    case LUNCH:
                        mealTypeString = LUNCH;
                        break;
                    case DINNER:
                        mealTypeString = DINNER;
                        break;
                    default:
                }
                deadlineTime = timeValue.get(mealTypeString);
            } catch (Exception e) {
                logger.error("Did not config deadline time ");
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return deadlineTime;
    }
    
    private static String getErrorMessageForCancel(LocalDate mealDay, LocalTime timeOfDay) {
        Map<String, String> substitutes = new HashMap<>();
        substitutes.put("hour", timeOfDay.toString());
        substitutes.put("date", Util.convertLocalDateToString(mealDay));
        return Util.replaceKeyOfMessage(substitutes, Translator.toLocale(BookingErrorCode.BookingMeal.OVER_DUE_DEADLINE_CANCEL_ORDER));
    }

    public void validateTimeScanQr(String timeScanQrMealJsonValue, BookingMealDetail bookingMealDetail) {
        MealType mealType = bookingMealDetail.getType();
        Map<String, String> qrScanTimeFrame = new HashMap<>();
        if (StringUtils.isEmpty(timeScanQrMealJsonValue)) {
            qrScanTimeFrame = Map.of(
                    BREAKFAST, "06:00-08:30",
                    LUNCH, "11:00-14:00",
                    DINNER, "17:00-19:30"
            );
        }
        else {
            JSONObject jsonObj = new JSONObject(timeScanQrMealJsonValue);
            for (String key : jsonObj.keySet()) {
                String value = jsonObj.getString(key);
                qrScanTimeFrame.put(key, value);
            }
        }


        switch (mealType) {
            case BREAKFAST:
                validateWithinTimeFrame(qrScanTimeFrame, BREAKFAST);
                break;
            case LUNCH:
                validateWithinTimeFrame(qrScanTimeFrame, LUNCH);
                break;
            case DINNER:
                validateWithinTimeFrame(qrScanTimeFrame, DINNER);
                break;
            default:
        }
    }

    public void validateWithinTimeFrame(Map<String, String> qrScanTimeFrame, String meal) {
        String timeFrame = qrScanTimeFrame.get(meal);
        String[] parts = timeFrame.split("-");
        LocalTime startTime = LocalTime.parse(parts[0]);
        LocalTime endTime = LocalTime.parse(parts[1]);
        LocalTime now = LocalTime.now();
        if (!isWithinTime(startTime, endTime, now)) {
            throw new BusinessEx(null, BookingErrorCode.BookingMeal.NOT_THE_TIME_SCAN_QR, null);
        }
    }

    private boolean isWithinTime(LocalTime startTime, LocalTime endTime, LocalTime now) {
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

}
