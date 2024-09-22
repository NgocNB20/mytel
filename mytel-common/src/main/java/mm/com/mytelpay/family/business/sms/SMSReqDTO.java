package mm.com.mytelpay.family.business.sms;

public class SMSReqDTO {
    private String address;
    private String content;
    private String number;
    private String sender;

    public SMSReqDTO() {
    }

    public String getAddress() {
        return this.address;
    }

    public String getContent() {
        return this.content;
    }

    public String getNumber() {
        return this.number;
    }

    public String getSender() {
        return this.sender;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SMSReqDTO)) {
            return false;
        } else {
            SMSReqDTO other = (SMSReqDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$address = this.getAddress();
                    Object other$address = other.getAddress();
                    if (this$address == null) {
                        if (other$address == null) {
                            break label59;
                        }
                    } else if (this$address.equals(other$address)) {
                        break label59;
                    }

                    return false;
                }

                Object this$content = this.getContent();
                Object other$content = other.getContent();
                if (this$content == null) {
                    if (other$content != null) {
                        return false;
                    }
                } else if (!this$content.equals(other$content)) {
                    return false;
                }

                Object this$number = this.getNumber();
                Object other$number = other.getNumber();
                if (this$number == null) {
                    if (other$number != null) {
                        return false;
                    }
                } else if (!this$number.equals(other$number)) {
                    return false;
                }

                Object this$sender = this.getSender();
                Object other$sender = other.getSender();
                if (this$sender == null) {
                    if (other$sender != null) {
                        return false;
                    }
                } else if (!this$sender.equals(other$sender)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SMSReqDTO;
    }

    public int hashCode() {
//        int PRIME = true;
        int result = 1;
        Object $address = this.getAddress();
        result = result * 59 + ($address == null ? 43 : $address.hashCode());
        Object $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : $content.hashCode());
        Object $number = this.getNumber();
        result = result * 59 + ($number == null ? 43 : $number.hashCode());
        Object $sender = this.getSender();
        result = result * 59 + ($sender == null ? 43 : $sender.hashCode());
        return result;
    }

    public String toString() {
        return "SMSRequest(address=" + this.getAddress() + ", content=" + this.getContent() + ", number=" + this.getNumber() + ", sender=" + this.getSender() + ")";
    }
}
