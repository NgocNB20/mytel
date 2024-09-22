package mm.com.mytelpay.family.exception;

public class BookingErrorCode extends CommonException{

    public static final class BookingCommon {

        private BookingCommon(){}

        public static final String FROM_DATE_LEST_THAN_TO_DATE = BASE_ERROR + "604";

        public static final String FROM_DATE_THAN_CURRENT_DATE = BASE_ERROR + "603";

        public static final String BOOKING_TIME_IS_EXISTS = BASE_ERROR + "815";

        public static final String FROM_DATE_INVALID = BASE_ERROR + "833";

        public static final String TO_DATE_INVALID = BASE_ERROR + "834";

    }
    public static final class BookingCar {
        private BookingCar(){}

        public static final String BOOKING_CAR_NOT_FOUND = BASE_ERROR + "810";

        public static final String BOOKING_CAR_DETAIL_NOT_FOUND = BASE_ERROR + "811";

        public static final String TIME_RETURN_INVALID = BASE_ERROR + "812";

        public static final String TIME_START_INVALID = BASE_ERROR + "813";

        public static final String ACCOUNT_NOT_ALLOWED = BASE_ERROR + "814";

        public static final String ORIGINAL_DIFFERENT_DESTINATION = BASE_ERROR + "816";

        public static final String DONT_HAVE_PERMISSION = BASE_ERROR + "818";

        public static final String ROLE_APPROVE_NOT_FOUND = BASE_ERROR + "820";

        public static final String INVALID_ROLE_APPROVE = BASE_ERROR + "821";

        public static final String INVALID_REJECT_BOOKING = BASE_ERROR + "822";

        public static final String INVALID_ROLE_REJECT = BASE_ERROR + "823";

        public static final String ROLE_REJECT_NOT_FOUND = BASE_ERROR + "824";

        public static final String INVALID_ROLE_EU = BASE_ERROR + "825";

        public static final String CANNOT_START_THE_TRIP_TODAY = BASE_ERROR + "826";

        public static final String CANNOT_UPDATE_THE_TRIP_ANYMORE = BASE_ERROR + "827";

        public static final String ONLY_DRIVER_CAN_COMPLETE_THE_TRIP = BASE_ERROR + "828";

        public static final String CANNOT_COMPLETE_THE_TRIP_TODAY = BASE_ERROR + "829";

        public static final String HAS_NOT_BEEN_ACCEPTED_YET = BASE_ERROR + "830";

        public static final String CANNOT_FINISH_THE_TRIP_WHEN_IT_HAS_NOT_STARTED = BASE_ERROR + "831";

        public static final String THE_RETURN_TRIP_CANNOT_BE_UPDATED_UNTIL_THE_OUTBOUND_TRIP_HAS_NOT_BEEN_COMPLETED = BASE_ERROR + "832";

        public static final String NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS = BASE_ERROR + "991";

        public static final String PHONE_IS_INVALID = BASE_ERROR + "835";

    }

    public static final class Notification {
        private Notification(){}

        public static final String NOT_FOUND = BASE_ERROR + "300";

        public static final String SEND_NOTIFICATION_FAIL = BASE_ERROR + "301";

    }

    public static final class Account {
        private Account(){}

        public static final String WRONG_INFO = BASE_ERROR + "989";

        public static final String NOT_FOUND = BASE_ERROR + "898";

        public static final String ACCOUNT_INACTIVE = BASE_ERROR + "913";

        public static final String ACCOUNT_HAS_NO_PERMISSIONS = BASE_ERROR + "818";

    }

    public static final class Resource {
        private Resource(){}

        public static final String CAR_NOT_FOUND = BASE_ERROR + "103";

        public static final String HOTEL_NOT_FOUND = BASE_ERROR + "113";

        public static final String MEAL_NOT_FOUND = BASE_ERROR + "123";

        public static final String CANTEEN_NOT_FOUND = BASE_ERROR + "133";
    }

    public static final class FileAttach {
        private FileAttach(){}

        public static final String CREATE_FILE_FAIL = BASE_ERROR + "010";
    }

    public static final class BookingMeal {
        private BookingMeal(){}

        public static final String BREAKFAST_OVER_DUE_TIME = BASE_ERROR + "600";

        public static final String LUNCH_OVER_DUE_TIME = BASE_ERROR + "601";

        public static final String DINNER_OVER_DUE_TIME = BASE_ERROR + "602";

        public static final String DAY_BOOKING_INVALID = BASE_ERROR + "605";

        public static final String NOT_ODER_DINNER_SATURDAY = BASE_ERROR + "606";

        public static final String NOT_ODER_SUNDAY = BASE_ERROR + "607";

        public static final String NUMBER_BOOKING_EXCEEDS_LIMIT = BASE_ERROR + "608";

        public static final String NOT_ENOUGH_MONEY = BASE_ERROR + "609";

        public static final String ORDER_ALREADY_CREATED = BASE_ERROR + "610";

        public static final String BOOKING_MEAL_NOT_FOUND = BASE_ERROR + "611";

        public static final String BOOKING_MEAL_DETAIL_NOT_FOUND = BASE_ERROR + "612";

        public static final String OVER_DUE_DEADLINE_CANCEL_ORDER = BASE_ERROR + "613";

        public static final String CANNOT_CANCEL_ORDER_MEAL = BASE_ERROR + "614";

        public static final String DONT_HAVE_PERMISSION = BASE_ERROR + "615";

        public static final String QR_IS_EXPIRED = BASE_ERROR + "616";

        public static final String CANNOT_SCAN_QR_TODAY = BASE_ERROR + "617";

        public static final String NOT_THE_TIME_SCAN_QR = BASE_ERROR + "618";


        public static final String CREATE_BOOKING_MEAL_FAIL = BASE_ERROR + "619";

        public static final String CREATE_BOOKING_MEAL_DETAIL_FAIL = BASE_ERROR + "620";

        public static final String NOT_SCAN_QR = BASE_ERROR + "621";

    }

    public static final class BookingHotel {
        private BookingHotel(){}

        public static final String BOOKING_HOTEL_NOT_FOUND = BASE_ERROR + "500";

        public static final String NOT_AUTHORIZED_BOOKING_HOTEL = BASE_ERROR + "501";

        public static final String TIME_BOOKING_INVALID = BASE_ERROR + "502";

        public static final String FEE_BOOKING_INVALID = BASE_ERROR + "503";

        public static final String FEE_SERVICE_INVALID = BASE_ERROR + "504";

    }
}
