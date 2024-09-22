package mm.com.mytelpay.family.utils;

import java.time.LocalTime;

public class Constants {

    private Constants(){

    }

    public static final String EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"+
            "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    
    public static final String TRANS_DATE_FORMAT = "yyyyMMddHHmmss";

    public static final Integer MAX_LIMIT = 300;

    public static final String CUSTOM_PIN_REGEX = "^[0-9]{6}$";

    public static final String OTP_REGEX = "^[0-9]{4}$";

    public static final String PHONE_NUMBER_PATTERN = "((0|)(9)\\d{7,9}|[+]?95(9)\\d{7,9})$";

    public static final Integer MAX_LIMIT_IMPORT_EXCEL = 501;

    public static final Integer MAX_LIMIT_200_IMPORT_EXCEL = 201;

    public static final String EXCEL_DATE_FORMATTER = "yyyy-MM-dd_HH_mm";

    public static final String NUMBER_REGEX = "^\\d+$";

    public static final String NUMBER_SEAT_REGEX = "^[1-9][0-9]*$";
    public static final String CHECK_EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static final String PASSWORD_PATTERN = "^[0-9]{6}$";

    public static final String POSITIVE_NUMBER_REGEX = "^[1-9]\\d*$";

    public static final LocalTime DEFAULT_BREAKFAST_BOOKING_OVER_DUE_TIME = LocalTime.of(0, 0);

    public static final LocalTime DEFAULT_LUNCH_BOOKING_OVER_DUE_TIME = LocalTime.of(10, 0);

    public static final LocalTime DEFAULT_DINNER_BOOKING_OVER_DUE_TIME = LocalTime.of(14, 0);

    public static final String APP_SECRET_KEY = "APP-SECRET-KEY";

    public static final Integer LIMIT_DAY_ORDER = 30;

    public static final String CONFIG_PRICE_MEAL = "config_meal_price";

    public static final String REGEX_RATING = "^[1-5]$";

    public static final String LOG_INFO_REST_TEMPLATE = "HTTP request sent. URL: {}, Response status: {}, Body: {}";

    public static final LocalTime DEFAULT_DEADLINE_TO_CANCEL_BREAK_FAST_ORDER = LocalTime.of(0, 0);

    public static final LocalTime DEFAULT_DEADLINE_TO_CANCEL_LUNCH_ORDER = LocalTime.of(8, 0);

    public static final LocalTime DEFAULT_DEADLINE_TO_CANCEL_DINNER_ORDER = LocalTime.of(14, 0);

    public static final String DEADLINE_TIME_BOOKING_MEAL_KEY = "deadline_time_booking_meal";

    public static final String DEADLINE_TIME_CANCEL_BOOKING_MEAL_KEY = "deadline_time_cancel_booking_meal";

}
