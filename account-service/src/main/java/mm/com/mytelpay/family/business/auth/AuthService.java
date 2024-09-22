package mm.com.mytelpay.family.business.auth;

import mm.com.mytelpay.family.models.CallAuthServiceDTO;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    CallAuthServiceDTO getFunctionFromJwt(String bearToken);

    boolean checkPermission(String requestUri, HttpServletRequest httpServletRequest);

    String getCurrentAccountId(HttpServletRequest httpServletRequest);

}
