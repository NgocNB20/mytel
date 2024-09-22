//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.otp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource({"file:${app.config.location}/otp.properties"})
@Configuration
@Component
public class OTPConfig {
    @Value("${otp.service.url}")
    private String url;
    @Value("${otp.service.connectionTimeout}")
    private Long connectionTimeout;
    @Value("${otp.service.readTimeout}")
    private Long readTimeout;
    @Value("${otp.service.writeTimeout}")
    private Long writeTimeout;

    public OTPConfig() {
    }

    public String getUrl() {
        return this.url;
    }

    public Long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public Long getReadTimeout() {
        return this.readTimeout;
    }

    public Long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setConnectionTimeout(final Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeout(final Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setWriteTimeout(final Long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof OTPConfig)) {
            return false;
        } else {
            OTPConfig other = (OTPConfig)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$connectionTimeout = this.getConnectionTimeout();
                    Object other$connectionTimeout = other.getConnectionTimeout();
                    if (this$connectionTimeout == null) {
                        if (other$connectionTimeout == null) {
                            break label59;
                        }
                    } else if (this$connectionTimeout.equals(other$connectionTimeout)) {
                        break label59;
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

                Object this$url = this.getUrl();
                Object other$url = other.getUrl();
                if (this$url == null) {
                    if (other$url != null) {
                        return false;
                    }
                } else if (!this$url.equals(other$url)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OTPConfig;
    }

    public int hashCode() {
        int result = 1;
        Object $connectionTimeout = this.getConnectionTimeout();
        result = result * 59 + ($connectionTimeout == null ? 43 : $connectionTimeout.hashCode());
        Object $readTimeout = this.getReadTimeout();
        result = result * 59 + ($readTimeout == null ? 43 : $readTimeout.hashCode());
        Object $writeTimeout = this.getWriteTimeout();
        result = result * 59 + ($writeTimeout == null ? 43 : $writeTimeout.hashCode());
        Object $url = this.getUrl();
        result = result * 59 + ($url == null ? 43 : $url.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getUrl();
        return "OTPConfig(url=" + var10000 + ", connectionTimeout=" + this.getConnectionTimeout() + ", readTimeout=" + this.getReadTimeout() + ", writeTimeout=" + this.getWriteTimeout() + ")";
    }
}
