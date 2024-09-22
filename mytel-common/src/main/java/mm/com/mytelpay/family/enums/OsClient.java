package mm.com.mytelpay.family.enums;

import lombok.Getter;

@Getter
public enum OsClient {
    IOS("ios"), ANDROID("android");
    OsClient(String value){
        this.value = value;
    }
    private String value;
}
