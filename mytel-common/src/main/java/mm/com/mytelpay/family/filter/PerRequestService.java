package mm.com.mytelpay.family.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Slf4j
@Service
@RequestScope
public class PerRequestService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${external.account.url}" + "/public/auth/checkPermission")
    private String checkPermissionUrl;
    @Value("${external.account.url}" + "/public/auth/getCurrentAccountId")
    private String getCurrentAccountIdUrl;

    public boolean checkValidPermission(String requestUri, HttpHeaders headers) {
        HttpEntity<String> reqEntity = new HttpEntity<>(requestUri, headers);
        ResponseEntity<Boolean> havePermissionResponseRes = restTemplate.exchange(checkPermissionUrl, HttpMethod.POST, reqEntity, Boolean.class);
        if (ObjectUtils.isEmpty(havePermissionResponseRes)) {
            log.debug("Call auth service is failed, please check auth-service is starting or not");
            return false;
        }
        Boolean havePermissionResponse = havePermissionResponseRes.getBody();

        return havePermissionResponse != null && havePermissionResponse;
    }

    public String getCurrentAccountId(HttpHeaders headers) {
        ResponseEntity<String> currentAccountIdRes = restTemplate.postForEntity(getCurrentAccountIdUrl, new HttpEntity<>(headers), String.class);
        if (ObjectUtils.isEmpty(currentAccountIdRes)) {
            log.debug("Cannot find current account_id from bear_token or call auth-service failed");
            return "";
        }
        return currentAccountIdRes.getBody();
    }

    public boolean isPublicURL(String url) {
        return url.startsWith("/public/");
    }

    public boolean isContainsUrl(String url, Set<String> urls) {
        return urls.contains(url);
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
