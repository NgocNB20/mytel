//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.otp;

public class SendOTPReqDTO {
    private String msisdn;
    private String requestId;
    private String currentTime;
    private String accountId;
    private String mobileDevice;
    private Boolean isTOTP;
    private String appVersion;

    public SendOTPReqDTO() {
    }

    public String getMsisdn() {
        return this.msisdn;
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

    public String getMobileDevice() {
        return this.mobileDevice;
    }

    public Boolean getIsTOTP() {
        return this.isTOTP;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public void setMsisdn(final String msisdn) {
        this.msisdn = msisdn;
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

    public void setMobileDevice(final String mobileDevice) {
        this.mobileDevice = mobileDevice;
    }

    public void setIsTOTP(final Boolean isTOTP) {
        this.isTOTP = isTOTP;
    }

    public void setAppVersion(final String appVersion) {
        this.appVersion = appVersion;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SendOTPReqDTO)) {
            return false;
        } else {
            SendOTPReqDTO other = (SendOTPReqDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label95: {
                    Object this$isTOTP = this.getIsTOTP();
                    Object other$isTOTP = other.getIsTOTP();
                    if (this$isTOTP == null) {
                        if (other$isTOTP == null) {
                            break label95;
                        }
                    } else if (this$isTOTP.equals(other$isTOTP)) {
                        break label95;
                    }

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

                Object this$requestId = this.getRequestId();
                Object other$requestId = other.getRequestId();
                if (this$requestId == null) {
                    if (other$requestId != null) {
                        return false;
                    }
                } else if (!this$requestId.equals(other$requestId)) {
                    return false;
                }

                label74: {
                    Object this$currentTime = this.getCurrentTime();
                    Object other$currentTime = other.getCurrentTime();
                    if (this$currentTime == null) {
                        if (other$currentTime == null) {
                            break label74;
                        }
                    } else if (this$currentTime.equals(other$currentTime)) {
                        break label74;
                    }

                    return false;
                }

                label67: {
                    Object this$accountId = this.getAccountId();
                    Object other$accountId = other.getAccountId();
                    if (this$accountId == null) {
                        if (other$accountId == null) {
                            break label67;
                        }
                    } else if (this$accountId.equals(other$accountId)) {
                        break label67;
                    }

                    return false;
                }

                Object this$mobileDevice = this.getMobileDevice();
                Object other$mobileDevice = other.getMobileDevice();
                if (this$mobileDevice == null) {
                    if (other$mobileDevice != null) {
                        return false;
                    }
                } else if (!this$mobileDevice.equals(other$mobileDevice)) {
                    return false;
                }

                Object this$appVersion = this.getAppVersion();
                Object other$appVersion = other.getAppVersion();
                if (this$appVersion == null) {
                    if (other$appVersion != null) {
                        return false;
                    }
                } else if (!this$appVersion.equals(other$appVersion)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SendOTPReqDTO;
    }

    public int hashCode() {
        int result = 1;
        Object $isTOTP = this.getIsTOTP();
        result = result * 59 + ($isTOTP == null ? 43 : $isTOTP.hashCode());
        Object $msisdn = this.getMsisdn();
        result = result * 59 + ($msisdn == null ? 43 : $msisdn.hashCode());
        Object $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : $requestId.hashCode());
        Object $currentTime = this.getCurrentTime();
        result = result * 59 + ($currentTime == null ? 43 : $currentTime.hashCode());
        Object $accountId = this.getAccountId();
        result = result * 59 + ($accountId == null ? 43 : $accountId.hashCode());
        Object $mobileDevice = this.getMobileDevice();
        result = result * 59 + ($mobileDevice == null ? 43 : $mobileDevice.hashCode());
        Object $appVersion = this.getAppVersion();
        result = result * 59 + ($appVersion == null ? 43 : $appVersion.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getMsisdn();
        return "SendOTPRequest(msisdn=" + var10000 + ", requestId=" + this.getRequestId() + ", currentTime=" + this.getCurrentTime() + ", accountId=" + this.getAccountId() + ", mobileDevice=" + this.getMobileDevice() + ", isTOTP=" + this.getIsTOTP() + ", appVersion=" + this.getAppVersion() + ")";
    }
}
