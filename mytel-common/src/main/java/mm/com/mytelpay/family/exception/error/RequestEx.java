package mm.com.mytelpay.family.exception.error;

public class RequestEx extends ErrorCommon {

    public RequestEx(String requestId, String errorCode, String message) {
        super(requestId, errorCode, message);
        if (message == null)
            setMessage(errorCode);
    }
    public RequestEx(String errorCode, String message) {
        super(errorCode, message);
        if (message == null)
            setMessage(errorCode);
    }
}
