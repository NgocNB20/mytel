//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.otp;

public class ActiveReqDTO {
    private String requestId;
    private String currentTime;
    private String msisdn;
    private String accountId;
    private String mobileDevice;
    private String pin;
    private String privateKey;

    ActiveReqDTO(final String requestId, final String currentTime, final String msisdn, final String accountId, final String mobileDevice, final String pin, final String privateKey) {
        this.requestId = requestId;
        this.currentTime = currentTime;
        this.msisdn = msisdn;
        this.accountId = accountId;
        this.mobileDevice = mobileDevice;
        this.pin = pin;
        this.privateKey = privateKey;
    }

    public static ActiveRequestBuilder builder() {
        return new ActiveRequestBuilder();
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

    public String getPrivateKey() {
        return this.privateKey;
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

    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ActiveReqDTO)) {
            return false;
        } else {
            ActiveReqDTO other = (ActiveReqDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label95: {
                    Object this$requestId = this.getRequestId();
                    Object other$requestId = other.getRequestId();
                    if (this$requestId == null) {
                        if (other$requestId == null) {
                            break label95;
                        }
                    } else if (this$requestId.equals(other$requestId)) {
                        break label95;
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

                Object this$msisdn = this.getMsisdn();
                Object other$msisdn = other.getMsisdn();
                if (this$msisdn == null) {
                    if (other$msisdn != null) {
                        return false;
                    }
                } else if (!this$msisdn.equals(other$msisdn)) {
                    return false;
                }

                label74: {
                    Object this$accountId = this.getAccountId();
                    Object other$accountId = other.getAccountId();
                    if (this$accountId == null) {
                        if (other$accountId == null) {
                            break label74;
                        }
                    } else if (this$accountId.equals(other$accountId)) {
                        break label74;
                    }

                    return false;
                }

                label67: {
                    Object this$mobileDevice = this.getMobileDevice();
                    Object other$mobileDevice = other.getMobileDevice();
                    if (this$mobileDevice == null) {
                        if (other$mobileDevice == null) {
                            break label67;
                        }
                    } else if (this$mobileDevice.equals(other$mobileDevice)) {
                        break label67;
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

                Object this$privateKey = this.getPrivateKey();
                Object other$privateKey = other.getPrivateKey();
                if (this$privateKey == null) {
                    if (other$privateKey != null) {
                        return false;
                    }
                } else if (!this$privateKey.equals(other$privateKey)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ActiveReqDTO;
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
        Object $privateKey = this.getPrivateKey();
        result = result * 59 + ($privateKey == null ? 43 : $privateKey.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getRequestId();
        return "ActiveRequest(requestId=" + var10000 + ", currentTime=" + this.getCurrentTime() + ", msisdn=" + this.getMsisdn() + ", accountId=" + this.getAccountId() + ", mobileDevice=" + this.getMobileDevice() + ", pin=" + this.getPin() + ", privateKey=" + this.getPrivateKey() + ")";
    }

    public static class ActiveRequestBuilder {
        private String requestId;
        private String currentTime;
        private String msisdn;
        private String accountId;
        private String mobileDevice;
        private String pin;
        private String privateKey;

        ActiveRequestBuilder() {
        }

        public ActiveRequestBuilder requestId(final String requestId) {
            this.requestId = requestId;
            return this;
        }

        public ActiveRequestBuilder currentTime(final String currentTime) {
            this.currentTime = currentTime;
            return this;
        }

        public ActiveRequestBuilder msisdn(final String msisdn) {
            this.msisdn = msisdn;
            return this;
        }

        public ActiveRequestBuilder accountId(final String accountId) {
            this.accountId = accountId;
            return this;
        }

        public ActiveRequestBuilder mobileDevice(final String mobileDevice) {
            this.mobileDevice = mobileDevice;
            return this;
        }

        public ActiveRequestBuilder pin(final String pin) {
            this.pin = pin;
            return this;
        }

        public ActiveRequestBuilder privateKey(final String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public ActiveReqDTO build() {
            return new ActiveReqDTO(this.requestId, this.currentTime, this.msisdn, this.accountId, this.mobileDevice, this.pin, this.privateKey);
        }

        public String toString() {
            return "ActiveRequest.ActiveRequestBuilder(requestId=" + this.requestId + ", currentTime=" + this.currentTime + ", msisdn=" + this.msisdn + ", accountId=" + this.accountId + ", mobileDevice=" + this.mobileDevice + ", pin=" + this.pin + ", privateKey=" + this.privateKey + ")";
        }
    }
}
