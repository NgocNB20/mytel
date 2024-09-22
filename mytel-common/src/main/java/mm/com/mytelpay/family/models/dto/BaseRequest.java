package mm.com.mytelpay.family.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import mm.com.mytelpay.family.logging.RequestUtils;
import org.springframework.util.StringUtils;


public class BaseRequest {
    @JsonProperty("requestId")
    String requestId;

    @JsonProperty("currentTime")
    String currentTime;

    public BaseRequest() {
    }

    public String getRequestId() {
        return  StringUtils.hasText(this.requestId)? this.requestId : RequestUtils.currentRequestId();
    }

    public String getCurrentTime() {
        return this.currentTime;
    }

    @JsonProperty("requestId")
    public void setRequestId(final String requestId) {
        this.requestId = StringUtils.hasText(requestId)? requestId : RequestUtils.currentRequestId();
    }

    @JsonProperty("currentTime")
    public void setCurrentTime(final String currentTime) {
        this.currentTime = currentTime;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BaseRequest)) {
            return false;
        } else {
            BaseRequest other = (BaseRequest)o;
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

                Object this$currentTime = this.getCurrentTime();
                Object other$currentTime = other.getCurrentTime();
                if (this$currentTime == null) {
                    if (other$currentTime != null) {
                        return false;
                    }
                } else if (!this$currentTime.equals(other$currentTime)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BaseRequest;
    }

    public int hashCode() {
        int result = 1;
        Object $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : $requestId.hashCode());
        Object $currentTime = this.getCurrentTime();
        result = result * 59 + ($currentTime == null ? 43 : $currentTime.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getRequestId();
        return "BaseRequest(requestId=" + var10000 + ", currentTime=" + this.getCurrentTime() + ")";
    }
}
