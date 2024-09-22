package mm.com.mytelpay.family.utils;

import mm.com.mytelpay.family.enums.TypeDateFilter;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class Util {

    public static Logger logger = LogManager.getLogger(Util.class);
    
    private Util() {
    }

    private static String defaultLanguage = "en";

    public static LocalDateTime convertToLocalDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        LocalDate dt = LocalDate.parse(dateStr, formatter);
        return dt.atStartOfDay();
    }
    
    public static LocalDateTime convertToLocalDateTimeStartOfDay(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        LocalDate dt = LocalDate.parse(dateStr, formatter);
        return dt.atStartOfDay();
    }
    
    public static LocalDateTime convertToLocalDateTimeEndOfDay(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        LocalDate dt = LocalDate.parse(dateStr, formatter);
        return dt.atTime(23, 59, 59);
    }

    public static LocalDate convertToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        return LocalDate.parse(dateStr, formatter);
    }

    public static String convertLocalDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static String convertLocalDateToString(LocalDate dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }

    public static LocalDateTime[] convertTypeDateFilter(TypeDateFilter type, LocalDateTime fromTime, LocalDateTime toTime) {

        if (type.equals(TypeDateFilter.TODAY)) {
            toTime = LocalDateTime.now();
            fromTime = toTime.with(LocalTime.MIN);
        } else if (type.equals(TypeDateFilter.LASTWEEK)) {
            fromTime = LocalDateTime.now().minusWeeks(1);
            toTime = LocalDateTime.now();
        } else if (type.equals(TypeDateFilter.LASTMONTH)) {
            fromTime = LocalDateTime.now().minusMonths(1);
            toTime = LocalDateTime.now();
        }
        LocalDateTime[] res = new LocalDateTime[2];
        res[0] = fromTime;
        res[1] = toTime;
        return res;
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TRANS_DATE_FORMAT);
        return dateFormat.format(new Date());
    }
    
    public static LocalDateTime getCurrentLocalDateTime(){
        return LocalDateTime.now();
    }

    public static String replaceMessageCode(String keyword, String message) {
        Map<String, String> substitutes = new HashMap<>();
        substitutes.put("fieldName", keyword);
        return new StringSubstitutor(substitutes).replace(message);
    }

    public static String replaceKeyOfMessage(Map<String, String> substitutes, String message) {
        return new StringSubstitutor(substitutes).replace(message);
    }

    public static boolean isMorthanOneDayFrom(LocalDateTime datetime){
        if(Objects.isNull(datetime)){
            return false;
        }
        
        LocalDate now  = LocalDate.now();
        return now.isAfter(datetime.toLocalDate());
    }
    
    public static String refineMobileNumber(String msisdn) {
        if (StringUtils.isBlank(msisdn)) {
            return msisdn;
        }
        if (msisdn.charAt(0) != '\b') {
            String countryCode = "95";
            if (String.valueOf(msisdn.charAt(0)).equals("0")) {
                return countryCode + msisdn.substring(1);
            }
        }
        return msisdn;
    }

    public static String unRefineMobileNumber(String msisdn) {
        String countryCode = "95";
        int lengthCode = countryCode.length();
        return msisdn != null && msisdn.length() > 5 && msisdn.substring(0, lengthCode).equals(countryCode) ? "0" + msisdn.substring(lengthCode, msisdn.length()) : msisdn;
    }

    public static CommonResponseDTO generateDefaultResponse(String requestId, Object result, Object... objects) {
        CommonResponseDTO commonResponse = new CommonResponseDTO("00000", Translator.toLocale("00000"), result);
        commonResponse.setRequestId(requestId);
        return commonResponse;
    }

    public static String getLanguage(HttpServletRequest httpServletRequest) {
        String lang = httpServletRequest.getHeader("Accept-Language");
        if (StringUtils.isEmpty(lang)) {
            lang = defaultLanguage;
        }
        return lang;
    }

    public static String substitute(Map<String, Object> substitutes, String source) {
        return StringSubstitutor.replace(source, substitutes, "{{", "}}");
    }

    public static boolean isValidJson(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public static String removeQuote(String keyword) {
        return org.apache.commons.lang3.StringUtils.strip(keyword, "\"");
    }

    public static boolean isRightCurrentAccountId(String bookingId, String currentAccountId) {
        return StringUtils.equals(bookingId, currentAccountId);
    }

    public static String getMsisdnFromJwt(String bearToken) {
        if (StringUtils.isEmpty(bearToken)) {
            return "";
        }
        String[] parts = bearToken.split("\\.");
        String msisdn = "";
        try {
            JSONObject payload = new JSONObject(decode(parts[1]));
            msisdn = payload.getString("preferred_username");
        } catch (JSONException e) {
            logger.error("Please check bearToken to get msisdn", e);
        }

        return msisdn;
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
}
