package mm.com.mytelpay.family.exception.error;

public class BookingEx extends ErrorCommon {

    public BookingEx(String requestId, String errorCode, String message) {
        super(requestId, errorCode, message);
        if (message == null)
            setMessage(errorCode);
    }
    public BookingEx(String errorCode, String message) {
        super(errorCode, message);
        if (message == null)
            setMessage(errorCode);
    }

}
