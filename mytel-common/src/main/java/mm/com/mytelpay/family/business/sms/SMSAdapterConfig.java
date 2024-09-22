//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource({"file:${app.config.location}/sms.properties"})
@Configuration
public class SMSAdapterConfig {
    @Value("${sms.smpp.url}")
    private String url;
    @Value("${sms.smpp.connectTimeout}")
    private Long connectTimeout;
    @Value("${sms.smpp.readTimeout}")
    private Long readTimeout;
    @Value("${sms.smpp.writeTimeout}")
    private Long writeTimeout;
    @Value("${sms.smpp.username}")
    private String username;
    @Value("${sms.smpp.password}")
    private String password;
    @Value("${sms.smpp.sender}")
    private String sender;

    public SMSAdapterConfig() {
    }

    public String getUrl() {
        return this.url;
    }

    public Long getConnectTimeout() {
        return this.connectTimeout;
    }

    public Long getReadTimeout() {
        return this.readTimeout;
    }

    public Long getWriteTimeout() {
        return this.writeTimeout;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getSender() {
        return this.sender;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setConnectTimeout(final Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(final Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setWriteTimeout(final Long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SMSAdapterConfig)) {
            return false;
        } else {
            SMSAdapterConfig other = (SMSAdapterConfig)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label95: {
                    Object this$connectTimeout = this.getConnectTimeout();
                    Object other$connectTimeout = other.getConnectTimeout();
                    if (this$connectTimeout == null) {
                        if (other$connectTimeout == null) {
                            break label95;
                        }
                    } else if (this$connectTimeout.equals(other$connectTimeout)) {
                        break label95;
                    }

                    return false;
                }

                Object this$readTimeout = this.getReadTimeout();
                Object other$readTimeout = other.getReadTimeout();
                if (this$readTimeout == null) {
                    if (other$readTimeout != null) {
                        return false;
                    }
                } else if (!this$readTimeout.equals(other$readTimeout)) {
                    return false;
                }

                Object this$writeTimeout = this.getWriteTimeout();
                Object other$writeTimeout = other.getWriteTimeout();
                if (this$writeTimeout == null) {
                    if (other$writeTimeout != null) {
                        return false;
                    }
                } else if (!this$writeTimeout.equals(other$writeTimeout)) {
                    return false;
                }

                label74: {
                    Object this$url = this.getUrl();
                    Object other$url = other.getUrl();
                    if (this$url == null) {
                        if (other$url == null) {
                            break label74;
                        }
                    } else if (this$url.equals(other$url)) {
                        break label74;
                    }

                    return false;
                }

                label67: {
                    Object this$username = this.getUsername();
                    Object other$username = other.getUsername();
                    if (this$username == null) {
                        if (other$username == null) {
                            break label67;
                        }
                    } else if (this$username.equals(other$username)) {
                        break label67;
                    }

                    return false;
                }

                Object this$password = this.getPassword();
                Object other$password = other.getPassword();
                if (this$password == null) {
                    if (other$password != null) {
                        return false;
                    }
                } else if (!this$password.equals(other$password)) {
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
        return other instanceof SMSAdapterConfig;
    }

    public int hashCode() {
//        int PRIME = true;
        int result = 1;
        Object $connectTimeout = this.getConnectTimeout();
        result = result * 59 + ($connectTimeout == null ? 43 : $connectTimeout.hashCode());
        Object $readTimeout = this.getReadTimeout();
        result = result * 59 + ($readTimeout == null ? 43 : $readTimeout.hashCode());
        Object $writeTimeout = this.getWriteTimeout();
        result = result * 59 + ($writeTimeout == null ? 43 : $writeTimeout.hashCode());
        Object $url = this.getUrl();
        result = result * 59 + ($url == null ? 43 : $url.hashCode());
        Object $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        Object $sender = this.getSender();
        result = result * 59 + ($sender == null ? 43 : $sender.hashCode());
        return result;
    }

    public String toString() {
        return "SMSAdapterConfig(url=" + this.getUrl() + ", connectTimeout=" + this.getConnectTimeout() + ", readTimeout=" + this.getReadTimeout() + ", writeTimeout=" + this.getWriteTimeout() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", sender=" + this.getSender() + ")";
    }
}
