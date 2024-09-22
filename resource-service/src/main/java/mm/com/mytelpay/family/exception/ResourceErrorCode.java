package mm.com.mytelpay.family.exception;

public class ResourceErrorCode extends CommonException{

    public static final class Car {
        private Car(){}

        public static final String NOT_FOUND = BASE_ERROR + "103";

        public static final String LICENSE_PLATE_IS_USED = BASE_ERROR + "101";

        public static final String IMPORT_FAIL = BASE_ERROR + "102";

        public static final String CAR_IN_TRIP = BASE_ERROR + "104";
        
        public static final String INVALID_QR = BASE_ERROR + "105";
        
        public static final String CAR_REGISTRATION_NOT_ACTIVE = BASE_ERROR + "106";
        
        public static final String QR_SCAN_HISTORY_NOT_FOUND = BASE_ERROR + "107";
    }

    public static final class Unit {

        private Unit(){}

        public static final String NOT_FOUND = BASE_ERROR + "880";

        public static final String UNIT_ALREADY_EXISTS_CANTEEN = BASE_ERROR + "011";

    }

    public static final class Food {
        private Food(){}

        public static final String NOT_FOUND = BASE_ERROR + "401";

    }

    public static final class Meal {
        private Meal(){}

        public static final String NOT_FOUND = BASE_ERROR + "123";

    }
    public static final class Menu {
        private Menu(){}

        public static final String NOT_FOUND = BASE_ERROR + "923";

        public static final String MENU_CREATED = BASE_ERROR + "918";
        public static final String REQUIRE_THREE_MEAL = BASE_ERROR + "919";
        public static final String REQUIRE_TWO_MEAL = BASE_ERROR + "920";
        public static final String NOT_CREATED_FOR_ONE = BASE_ERROR + "921";
        public static final String CHOOSE_MAXIMUM_FOOD = BASE_ERROR + "922";
    }

    public static final class Canteen {
        private Canteen(){}

        public static final String NOT_FOUND = BASE_ERROR + "133";

        public static final String DELETED_FAIL = BASE_ERROR + "134";

        public static final String IS_EXIST = BASE_ERROR + "135";

        public static final String IMPORT_FAIL = BASE_ERROR + "136";

        public static final String NUMBERSEAT = BASE_ERROR + "137";

    }

    public static final class Hotel {
        private Hotel(){}

        public static final String NOT_FOUND = BASE_ERROR + "113";

        public static final String DELETE_FAIL = BASE_ERROR + "114";

        public static final String IMPORT_FAIL = BASE_ERROR + "115";

        public static final String CODE_IS_EXISTS = BASE_ERROR + "116";

        public static final String HOTEL_BOOKED_CANNOT_BE_DELETED = BASE_ERROR + "117";
         
    }    
    public static final class Province {
        private Province(){}

        public static final String NOT_FOUND = BASE_ERROR + "140";

        public static final String CODE_IS_EXISTS = BASE_ERROR + "141";

        public static final String NAME_IS_EXISTS = BASE_ERROR + "142";

        public static final String IMPORT_FAIL = BASE_ERROR + "143";

        public static final String IS_USED = BASE_ERROR + "144";


    }

    public static final class District {
        private District(){}

        public static final String NOT_FOUND = BASE_ERROR + "150";

        public static final String DUPLICATE_CODE = BASE_ERROR + "151";

        public static final String IMPORT_FAIL = BASE_ERROR + "152";
        public static final String NAME_IS_EXISTS = BASE_ERROR + "153";
        public static final String RELATED_HOTEL = BASE_ERROR + "154";

        public static final String NOT_PART_OF_PROVINCE = BASE_ERROR + "118";

    }

    public static final class ApplicationSetting {
        private ApplicationSetting(){}

        public static final String NOT_FOUND = BASE_ERROR + "702";

        public static final String KEY_IS_USED = BASE_ERROR + "701";
    }

    public static final class Account {
        private Account(){}

        public static final String NOT_FOUND = BASE_ERROR + "898";
    }
}
