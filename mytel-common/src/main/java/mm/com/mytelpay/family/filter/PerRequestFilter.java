package mm.com.mytelpay.family.filter;

import mm.com.mytelpay.family.config.NoNeedAuthorUrls;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class PerRequestFilter extends OncePerRequestFilter {
    @Autowired
    private PerRequestService perRequestService;
    @Autowired
    private PerRequestContextDto perRequestContextDto;

    /*
     * This method is used to check the authorization for each request and set important information related to the current logged-in user.
     *
     * It performs the following tasks:
     * 1. Checks the authorization and authentication requirements based on the request URI.
     * 2. Retrieves the JSON Web Token (JWT) from the request headers and extracts the current user's account ID.
     * 3. Sets the retrieved JWT and account ID in the perRequestContext to make them accessible throughout the request handling process.
     * 4. Validates the permission (authorization) for the requested URI, ensuring the current user has the necessary access rights.
     * 5. If the URI is listed as a public URL, authentication and authorization checks are skipped.
     * 6. If the user lacks permission to access the requested URI, a 403 Forbidden error is returned.
     * 7. Finally, the request is passed to the next filter in the chain for further processing.
     *
     * Note: If the request URI is a public URL, no authentication, authorization, or JWT verification is required.
     *
     * It is important to call this method within the filter chain to enforce the authorization rules and ensure the appropriate actions are taken based on the request and user's permissions.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String requestUri = httpServletRequest.getRequestURI();
        if (perRequestService.isPublicURL(requestUri)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String bearerToken = perRequestService.getJwtFromRequest(httpServletRequest);
        if (StringUtils.isEmpty(bearerToken)) {
            logger.error("Doesn't have Authorization header");
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, null);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        String currentUserId = perRequestService.getCurrentAccountId(headers);
        perRequestContextDto.setBearToken(bearerToken);
        perRequestContextDto.setCurrentAccountId(currentUserId);

        Set<String> noNeedAuthorUrls = NoNeedAuthorUrls.getInstance().getUrls();
        if (perRequestService.isContainsUrl(requestUri, noNeedAuthorUrls)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        boolean havePermission = perRequestService.checkValidPermission(requestUri, headers);
        if (!havePermission) {
            logger.error("Current user doesn't have permission to access this url");
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, null);
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
