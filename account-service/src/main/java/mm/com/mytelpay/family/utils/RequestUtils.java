package mm.com.mytelpay.family.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RequestUtils {
    private RequestUtils() {
    }

    public static Map<String, Object> getHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null)
            return new HashMap<>();

        Map<String, Object> result             = new HashMap<>();
        Iterator<String>    headerNameIterator = headerNames.asIterator();
        while (headerNameIterator.hasNext()) {
            String key = headerNameIterator.next();
            result.put(key, request.getHeader(key));
        }
        return result;
    }

    public static String getHeader(HttpServletRequest request, String header) {
        return getHeaders(request).entrySet().stream()
                .filter(f -> f.getKey().toLowerCase().trim().equals(header.toLowerCase().trim()))
                .map(f -> (String) f.getValue())
                .findFirst()
                .orElse(null);
    }

    public static void writeJsonResponse(HttpServletResponse response, Object object, HttpStatus statusCode) throws IOException {
        ObjectMapper writer = new ObjectMapper();
        response.setStatus(statusCode.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        writer.writeValue(response.getWriter(), object);
    }
}
