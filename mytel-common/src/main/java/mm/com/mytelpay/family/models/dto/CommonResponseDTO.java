package mm.com.mytelpay.family.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class CommonResponseDTO {
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("errorCode")
    private String errorCode;
    @JsonProperty("responseTime")
    @JsonFormat(
        shape = Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
        timezone = "Asia/Yangon"
    )
    @JsonDeserialize(
        using = LocalDateTimeDeserializer.class
    )
    @JsonSerialize(
        using = LocalDateTimeSerializer.class
    )
    private LocalDateTime responseTime = LocalDateTime.now();
    @JsonProperty("message")
    private String message;
    @JsonProperty("signature")
    private String signature;
    @JsonProperty("result")
    private Object result;

    public CommonResponseDTO(String errorCode, String message, Object result) {
        this.errorCode = errorCode;
        this.message = message;
        this.result = result;
    }

    public static CommonResponseBuilder builder() {
        return new CommonResponseBuilder();
    }

    public CommonResponseDTO(final String requestId, final String errorCode, final LocalDateTime responseTime, final String message, final String signature, final Object result) {
        this.requestId = requestId;
        this.errorCode = errorCode;
        this.responseTime = responseTime;
        this.message = message;
        this.signature = signature;
        this.result = result;
    }

    public CommonResponseDTO() {
    }

    public String getRequestId() {
        return this.requestId;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public LocalDateTime getResponseTime() {
        return this.responseTime;
    }

    public String getMessage() {
        return this.message;
    }

    public String getSignature() {
        return this.signature;
    }

    public Object getResult() {
        return this.result;
    }

    @JsonProperty("requestId")
    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    @JsonProperty("errorCode")
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty("responseTime")
    @JsonFormat(
        shape = Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
        timezone = "Asia/Yangon"
    )
    @JsonDeserialize(
        using = LocalDateTimeDeserializer.class
    )
    public void setResponseTime(final LocalDateTime responseTime) {
        this.responseTime = responseTime;
    }

    @JsonProperty("message")
    public void setMessage(final String message) {
        this.message = message;
    }

    @JsonProperty("signature")
    public void setSignature(final String signature) {
        this.signature = signature;
    }

    @JsonProperty("result")
    public void setResult(final Object result) {
        this.result = result;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CommonResponseDTO)) {
            return false;
        } else {
            CommonResponseDTO other = (CommonResponseDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$requestId = this.getRequestId();
                Object other$requestId = other.getRequestId();
                if (this$requestId == null) {
                    if (other$requestId != null) {
                        return false;
                    }
                } else if (!this$requestId.equals(other$requestId)) {
                    return false;
                }

                Object this$errorCode = this.getErrorCode();
                Object other$errorCode = other.getErrorCode();
                if (this$errorCode == null) {
                    if (other$errorCode != null) {
                        return false;
                    }
                } else if (!this$errorCode.equals(other$errorCode)) {
                    return false;
                }

                Object this$responseTime = this.getResponseTime();
                Object other$responseTime = other.getResponseTime();
                if (this$responseTime == null) {
                    if (other$responseTime != null) {
                        return false;
                    }
                } else if (!this$responseTime.equals(other$responseTime)) {
                    return false;
                }

                label62: {
                    Object this$message = this.getMessage();
                    Object other$message = other.getMessage();
                    if (this$message == null) {
                        if (other$message == null) {
                            break label62;
                        }
                    } else if (this$message.equals(other$message)) {
                        break label62;
                    }

                    return false;
                }

                label55: {
                    Object this$signature = this.getSignature();
                    Object other$signature = other.getSignature();
                    if (this$signature == null) {
                        if (other$signature == null) {
                            break label55;
                        }
                    } else if (this$signature.equals(other$signature)) {
                        break label55;
                    }

                    return false;
                }

                Object this$result = this.getResult();
                Object other$result = other.getResult();
                if (this$result == null) {
                    if (other$result != null) {
                        return false;
                    }
                } else if (!this$result.equals(other$result)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CommonResponseDTO;
    }

    public int hashCode() {
        int result = 1;
        Object $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : $requestId.hashCode());
        Object $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        Object $responseTime = this.getResponseTime();
        result = result * 59 + ($responseTime == null ? 43 : $responseTime.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $signature = this.getSignature();
        result = result * 59 + ($signature == null ? 43 : $signature.hashCode());
        Object $result = this.getResult();
        result = result * 59 + ($result == null ? 43 : $result.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getRequestId();
        return "CommonResponse(requestId=" + var10000 + ", errorCode=" + this.getErrorCode() + ", responseTime=" + this.getResponseTime() + ", message=" + this.getMessage() + ", signature=" + this.getSignature() + ", result=" + this.getResult() + ")";
    }

    public static class CommonResponseBuilder {
        private String requestId;
        private String errorCode;
        private LocalDateTime responseTime;
        private String message;
        private String signature;
        private Object result;

        CommonResponseBuilder() {
        }

        @JsonProperty("requestId")
        public CommonResponseBuilder requestId(final String requestId) {
            this.requestId = requestId;
            return this;
        }

        @JsonProperty("errorCode")
        public CommonResponseBuilder errorCode(final String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        @JsonProperty("responseTime")
        @JsonFormat(
            shape = Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
            timezone = "Asia/Yangon"
        )
        @JsonDeserialize(
            using = LocalDateTimeDeserializer.class
        )
        public CommonResponseBuilder responseTime(final LocalDateTime responseTime) {
            this.responseTime = responseTime;
            return this;
        }

        @JsonProperty("message")
        public CommonResponseBuilder message(final String message) {
            this.message = message;
            return this;
        }

        @JsonProperty("signature")
        public CommonResponseBuilder signature(final String signature) {
            this.signature = signature;
            return this;
        }

        @JsonProperty("result")
        public CommonResponseBuilder result(final Object result) {
            this.result = result;
            return this;
        }

        public CommonResponseDTO build() {
            return new CommonResponseDTO(this.requestId, this.errorCode, this.responseTime, this.message, this.signature, this.result);
        }

        public String toString() {
            return "CommonResponse.CommonResponseBuilder(requestId=" + this.requestId + ", errorCode=" + this.errorCode + ", responseTime=" + this.responseTime + ", message=" + this.message + ", signature=" + this.signature + ", result=" + this.result + ")";
        }
    }
}
