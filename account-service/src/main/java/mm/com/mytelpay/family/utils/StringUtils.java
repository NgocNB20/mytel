package mm.com.mytelpay.family.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class StringUtils {

    public static String removeQuote(String keyword) {
        return org.apache.commons.lang3.StringUtils.strip(keyword, "\"");
    }

    public static String replaceMessageCode(String keyword, String message){
        Map<String, String> substitutes = new HashMap<>();
        substitutes.put("fieldName",keyword);
        return new StringSubstitutor(substitutes).replace(message);
    }

}
