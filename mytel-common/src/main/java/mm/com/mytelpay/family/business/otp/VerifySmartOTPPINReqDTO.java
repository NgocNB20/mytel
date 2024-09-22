package mm.com.mytelpay.family.business.otp;

public class VerifySmartOTPPINReqDTO {
    private String requestId;
    private String currentTime;
    private String msisdn;
    private String accountId;
    private String mobileDevice;
    private String pin;

    public VerifySmartOTPPINReqDTO() {
    }

    public String getRequestId() {
        return this.requestId;
    }

    public String getCurrentTime() {
        return this.currentTime;
    }

    public String getMsisdn() {
        return this.msisdn;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getMobileDevice() {
        return this.mobileDevice;
    }

    public String getPin() {
        return this.pin;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public void setCurrentTime(final String currentTime) {
        this.currentTime = currentTime;
    }

    public void setMsisdn(final String msisdn) {
        this.msisdn = msisdn;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public void setMobileDevice(final String mobileDevice) {
        this.mobileDevice = mobileDevice;
    }

    public void setPin(final String pin) {
        this.pin = pin;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof VerifySmartOTPPINReqDTO)) {
            return false;
        } else {
            VerifySmartOTPPINReqDTO other = (VerifySmartOTPPINReqDTO)o;
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

                Object this$msisdn = this.getMsisdn();
                Object other$msisdn = other.getMsisdn();
                if (this$msisdn == null) {
                    if (other$msisdn != null) {
                        return false;
                    }
                } else if (!this$msisdn.equals(other$msisdn)) {
                    return false;
                }

                label62: {
                    Object this$accountId = this.getAccountId();
                    Object other$accountId = other.getAccountId();
                    if (this$accountId == null) {
                        if (other$accountId == null) {
                            break label62;
                        }
                    } else if (this$accountId.equals(other$accountId)) {
                        break label62;
                    }

                    return false;
                }

                label55: {
                    Object this$mobileDevice = this.getMobileDevice();
                    Object other$mobileDevice = other.getMobileDevice();
                    if (this$mobileDevice == null) {
                        if (other$mobileDevice == null) {
                            break label55;
                        }
                    } else if (this$mobileDevice.equals(other$mobileDevice)) {
                        break label55;
                    }

                    return false;
                }

                Object this$pin = this.getPin();
                Object other$pin = other.getPin();
                if (this$pin == null) {
                    if (other$pin != null) {
                        return false;
                    }
                } else if (!this$pin.equals(other$pin)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof VerifySmartOTPPINReqDTO;
    }

    public int hashCode() {
        int result = 1;
        Object $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : $requestId.hashCode());
        Object $currentTime = this.getCurrentTime();
        result = result * 59 + ($currentTime == null ? 43 : $currentTime.hashCode());
        Object $msisdn = this.getMsisdn();
        result = result * 59 + ($msisdn == null ? 43 : $msisdn.hashCode());
        Object $accountId = this.getAccountId();
        result = result * 59 + ($accountId == null ? 43 : $accountId.hashCode());
        Object $mobileDevice = this.getMobileDevice();
        result = result * 59 + ($mobileDevice == null ? 43 : $mobileDevice.hashCode());
        Object $pin = this.getPin();
        result = result * 59 + ($pin == null ? 43 : $pin.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getRequestId();
        return "VerifySmartOTPPINRequest(requestId=" + var10000 + ", currentTime=" + this.getCurrentTime() + ", msisdn=" + this.getMsisdn() + ", accountId=" + this.getAccountId() + ", mobileDevice=" + this.getMobileDevice() + ", pin=" + this.getPin() + ")";
    }
}
