package mm.com.mytelpay.family.exception;

public class AccountErrorCode extends CommonException {

    static final String BASE_ERROR = "12";

    public static final class Business {
        private Business(){}

        public static final String SUCCESS = "00000";

        public static final String WRONG_PIN = BASE_ERROR + "903";

        public static final String ACCESS_DENIED = BASE_ERROR + "996";

        public static final String UNAUTHORIZED = BASE_ERROR + "994";

        public static final String MAXIMUM_REFRESH_TOKEN = BASE_ERROR + "911";

        public static final String LOGIN_FAIL = BASE_ERROR + "907";

        public static final String LOG_OUT_FAILED = BASE_ERROR + "908";

    }

    public static final class Request {
        private Request(){}

        public static final String INPUT_INVALID = BASE_ERROR + "992";

        public static final String INPUT_IS_REQUIRED = BASE_ERROR + "993";

    }

    public static final class ACCOUNT {
        private ACCOUNT(){}

        public static final String NOT_FOUND = BASE_ERROR + "000";

        public static final String WRONG_PASS = BASE_ERROR + "001";
        public static final String EMAIL_EXISTED = BASE_ERROR + "003";
        public static final String PASS_NOT_MATCH = BASE_ERROR + "004";
        public static final String PHONE_EXISTED = BASE_ERROR + "008";

        public static final String CREATE_FAIL = BASE_ERROR + "003";

        public static final String CURRENT_PASS_WRONG = BASE_ERROR + "013";

        public static final String ONLY_ALLOW_DELETING_INACTIVE_USER = BASE_ERROR + "012";

        public static final String ACCOUNT_HAS_NOT_BEEN_APPROVED = BASE_ERROR + "017";

        public static final String THE_ACCOUNT_CAN_ONLY_BE_DELETED_AFTER_COMPLETING_ALL_BOOKINGS = BASE_ERROR + "018";

        public static final String PHONE_NOT_EXISTS = BASE_ERROR + "770";

        public static final String EMAIL_NOT_EXISTS = BASE_ERROR + "771";

        public static final String PHONE_OR_EMAIL_NOT_MATCH = BASE_ERROR + "772";

        public static final String ACCOUNT_ID_NOT_FOUND = BASE_ERROR + "773";

        public static final String NOT_ALLOW_UPDATE = BASE_ERROR + "774";

        public static final String USER_CANNOT_BE_DELETE = BASE_ERROR + "775";

        public static final String EXISTED = BASE_ERROR + "008";

        public static final String REGISTER_ACCOUNT_FAIL = BASE_ERROR + "205";

        public static final String REGISTER_FAIL = BASE_ERROR + "981";

        public static final String INCORRECT_PHONE_NUMBER_OR_PASS = BASE_ERROR + "890";

        public static final String VERIFY_OTP_FAIL = BASE_ERROR + "010";

        public static final String THE_ACCOUNT_INFORMATION_IS_INCORRECT_OR_HAS_NOT_BEEN_ACTIVATED = BASE_ERROR + "912";

        public static final String THE_ACCOUNT_HAS_BEEN_LOCKED = BASE_ERROR + "914";

        public static final String BALANCE_NOT_ENOUGH = BASE_ERROR + "915";

        public static final String CHANGE_PASS_UNSUCCESSFULLY = BASE_ERROR + "906";

        public static final String WRONG_INFO = BASE_ERROR + "989";

        public static final String DOES_NOT_EXISTS = BASE_ERROR + "773";

        public static final String IMPORT_FAIL = BASE_ERROR + "102";

        public static final String CANNOT_TOPUP_FOR_CHEF = BASE_ERROR + "916";

        public static final String TIME_FROM_TIME_TO_INVALID = BASE_ERROR + "917";

        public static final String NOT_AUTHORIZED = BASE_ERROR + "818";

        public static final String ACCOUNT_APPROVED = BASE_ERROR + "778";

        public static final String NO_PERMISSION_CHEF_APPROVAL = BASE_ERROR + "779";

    }

    public static final class ActionLog {
        private ActionLog(){}

        public static final String NOT_FOUND = BASE_ERROR + "700";

        public static final String INVALID_STATE = BASE_ERROR + "701";

        public static final String WRONG_REQUEST = "999";

        public static final String TIME_OUT = BASE_ERROR + "702";

        public static final String UNVERIFIED = BASE_ERROR + "703";
    }

    public static final class Role {
        private Role(){}

        public static final String EXISTED = BASE_ERROR + "100";

        public static final String NOT_FOUND = BASE_ERROR + "101";

        public static final String ONE_OF_NOT_FOUND = BASE_ERROR + "102";

        public static final String ROLE_CODE_WAS_EXISTS = BASE_ERROR + "885";

        public static final String ROLE_DOES_NOT_EXISTS = BASE_ERROR + "886";

        public static final String NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS = BASE_ERROR + "991";

        public static final String ROLE_ASSIGNED_TO_THE_USER = BASE_ERROR + "887";
    }

    public static final class Function {
        private Function(){}

        public static final String PARENT_ID_INVALID = BASE_ERROR + "776";

        public static final String CODE_EXISTED = BASE_ERROR + "883";

        public static final String FUNCTION_ID_NOT_EXISTED = BASE_ERROR + "882";

        public static final String FUNCTION_NOT_EXISTED = BASE_ERROR + "884";

        public static final String FUNCTION_ASSIGNED = BASE_ERROR + "881";

        public static final String ONE_OF_NOT_FOUND = BASE_ERROR + "202";

        public static final String CANNOT_DELETE_THIS_FUNCTION_BECAUSE_IT_HAS_OTHER_FUNCTIONS_THAT_ARE_USED_AS_THE_PARENT = BASE_ERROR + "879";
    }

    public static final class Keycloak {
        private Keycloak(){}

        public static final String CREATE_FAIL = BASE_ERROR + "207";

    }

    public static final class Unit {
        private Unit(){}

        public static final String NOT_FOUND = BASE_ERROR + "880";

        public static final String IS_ASSIGNED = BASE_ERROR + "881";

        public static final String EMPTY = BASE_ERROR + "991";

        public static final String IS_EXIST = BASE_ERROR + "990";

        public static final String CANNOT_DELETE = BASE_ERROR + "777";

    }

    public static final class OTP {
        private OTP(){}

        public static final String OTP_IS_INVALID = BASE_ERROR + "014";
    }

    public static final class Canteen {
        public static final String CANTEEN_NOT_FOUND = BASE_ERROR + "133";
    }
}
