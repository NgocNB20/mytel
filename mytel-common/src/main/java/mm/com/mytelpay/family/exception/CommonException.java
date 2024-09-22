package mm.com.mytelpay.family.exception;

public class CommonException {

    static final String BASE_ERROR = "12";

    public static final class Business {

        public static final String SUCCESS = "00000";

        public static final String WRONG_PIN = BASE_ERROR + "903";

        public static final String ACCESS_DENIED = BASE_ERROR + "996";

        public static final String UNAUTHORIZED =  BASE_ERROR + "994";

        public static final String SYSTEM_BUSY = BASE_ERROR + "002";

        public static final String NO_DATA_FOUND = BASE_ERROR + "991";
    }

    public static final class Storage {
        public static final String FILE_SIZE_EXCEEDS_LIMIT = BASE_ERROR + "801";
    }

    public static final class Request {

        public static final String INPUT_INVALID = BASE_ERROR + "992";

        public static final String INPUT_IS_REQUIRED = BASE_ERROR + "993";

    }

    public static final class File {

        public static final String FILE_SIZE_EXCEEDS_LIMIT = BASE_ERROR + "801";

        public static final String LIMIT_ROW_IMPORT = BASE_ERROR + "803";

    }
}

