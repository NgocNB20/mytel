//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.config.http;

import java.util.Map;

public class HttpConfig {
    private String url;
    private Long connectionTimeout;
    private Long readTimeout;
    private Long writeTimeout;
    private String publicKey;
    private String keyEncrypt;
    private Map<String, String> headerMaps;

    public HttpConfig(String url, Long connectionTimeout, Long readTimeout, Long writeTimeout, Map<String, String> headerMaps) {
        this.url = url;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.headerMaps = headerMaps;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Long getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setWriteTimeout(Long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Map<String, String> getHeaderMaps() {
        return this.headerMaps;
    }

    public void setHeaderMaps(Map<String, String> headerMaps) {
        this.headerMaps = headerMaps;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getKeyEncrypt() {
        return this.keyEncrypt;
    }

    public void setKeyEncrypt(String keyEncrypt) {
        this.keyEncrypt = keyEncrypt;
    }
}
