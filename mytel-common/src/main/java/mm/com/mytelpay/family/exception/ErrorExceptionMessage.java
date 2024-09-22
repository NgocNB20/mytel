package mm.com.mytelpay.family.exception;

import mm.com.mytelpay.family.models.dto.CommonResponseDTO;

public class ErrorExceptionMessage extends CommonResponseDTO {
    public ErrorExceptionMessage(String requestId, String status, String message, Object[]... objects) {
        super("999", message, (Object)null);
        this.setRequestId(requestId);
        this.setErrorCode(status);
        this.setMessage(message);
    }
}
