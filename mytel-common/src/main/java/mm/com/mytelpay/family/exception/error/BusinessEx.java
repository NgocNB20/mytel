package mm.com.mytelpay.family.exception.error;


public class BusinessEx extends ErrorCommon {

    public BusinessEx(String requestId, String errorCode, String message) {
        super(requestId, errorCode, message);
        if (message == null)
            setMessage(errorCode);
    }
    public BusinessEx(String errorCode, String message) {
        super(errorCode, message);
        if (message == null)
            setMessage(errorCode);
    }
}
