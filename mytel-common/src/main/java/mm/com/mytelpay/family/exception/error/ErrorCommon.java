package mm.com.mytelpay.family.exception.error;

public class  ErrorCommon extends RuntimeException {
    private String requestId;
    private String errorCode;
    private String message;

    public ErrorCommon(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCommon(String requestId, String errorCode, String message) {
        this.requestId = requestId;
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
