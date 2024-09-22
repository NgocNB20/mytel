package mm.com.mytelpay.family.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class StringUtils {

    public static String removeQuote(String keyword) {
        return org.apache.commons.lang3.StringUtils.strip(keyword, "\"");
    }

}
