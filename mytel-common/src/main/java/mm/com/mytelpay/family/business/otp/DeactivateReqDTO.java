//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.otp;

public class DeactivateReqDTO {
    private String requestId;
    private String currentTime;
    private String accountId;
    private String role;

    DeactivateReqDTO(final String requestId, final String currentTime, final String accountId, final String role) {
        this.requestId = requestId;
        this.currentTime = currentTime;
        this.accountId = accountId;
        this.role = role;
    }

    public static DeactivateRequestBuilder builder() {
        return new DeactivateRequestBuilder();
    }

    public String getRequestId() {
        return this.requestId;
    }

    public String getCurrentTime() {
        return this.currentTime;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getRole() {
        return this.role;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public void setCurrentTime(final String currentTime) {
        this.currentTime = currentTime;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DeactivateReqDTO)) {
            return false;
        } else {
            DeactivateReqDTO other = (DeactivateReqDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$requestId = this.getRequestId();
                    Object other$requestId = other.getRequestId();
                    if (this$requestId == null) {
                        if (other$requestId == null) {
                            break label59;
                        }
                    } else if (this$requestId.equals(other$requestId)) {
                        break label59;
                    }

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

                Object this$accountId = this.getAccountId();
                Object other$accountId = other.getAccountId();
                if (this$accountId == null) {
                    if (other$accountId != null) {
                        return false;
                    }
                } else if (!this$accountId.equals(other$accountId)) {
                    return false;
                }

                Object this$role = this.getRole();
                Object other$role = other.getRole();
                if (this$role == null) {
                    if (other$role != null) {
                        return false;
                    }
                } else if (!this$role.equals(other$role)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DeactivateReqDTO;
    }

    public int hashCode() {
        int result = 1;
        Object $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : $requestId.hashCode());
        Object $currentTime = this.getCurrentTime();
        result = result * 59 + ($currentTime == null ? 43 : $currentTime.hashCode());
        Object $accountId = this.getAccountId();
        result = result * 59 + ($accountId == null ? 43 : $accountId.hashCode());
        Object $role = this.getRole();
        result = result * 59 + ($role == null ? 43 : $role.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getRequestId();
        return "DeactivateRequest(requestId=" + var10000 + ", currentTime=" + this.getCurrentTime() + ", accountId=" + this.getAccountId() + ", role=" + this.getRole() + ")";
    }

    public static class DeactivateRequestBuilder {
        private String requestId;
        private String currentTime;
        private String accountId;
        private String role;

        DeactivateRequestBuilder() {
        }

        public DeactivateRequestBuilder requestId(final String requestId) {
            this.requestId = requestId;
            return this;
        }

        public DeactivateRequestBuilder currentTime(final String currentTime) {
            this.currentTime = currentTime;
            return this;
        }

        public DeactivateRequestBuilder accountId(final String accountId) {
            this.accountId = accountId;
            return this;
        }

        public DeactivateRequestBuilder role(final String role) {
            this.role = role;
            return this;
        }

        public DeactivateReqDTO build() {
            return new DeactivateReqDTO(this.requestId, this.currentTime, this.accountId, this.role);
        }

        public String toString() {
            return "DeactivateRequest.DeactivateRequestBuilder(requestId=" + this.requestId + ", currentTime=" + this.currentTime + ", accountId=" + this.accountId + ", role=" + this.role + ")";
        }
    }
}
