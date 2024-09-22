package mm.com.mytelpay.family.business.auth;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mm.com.mytelpay.family.business.BaseBusiness;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.model.entities.Function;
import mm.com.mytelpay.family.models.CallAuthServiceDTO;
import mm.com.mytelpay.family.models.CustomAuthority;
import mm.com.mytelpay.family.repo.AccountRepository;
import mm.com.mytelpay.family.repo.FunctionRepository;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Service
public class AuthServiceImpl extends BaseBusiness implements AuthService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    FunctionRepository functionRepository;
    @Autowired
    PerRequestContextDto contextDto;

    @Override
    public CallAuthServiceDTO getFunctionFromJwt(String bearToken) {
        CallAuthServiceDTO callAuthServiceDto = new CallAuthServiceDTO();
        String[] parts = bearToken.split("\\.");

        JSONObject payload = new JSONObject(decode(parts[1]));
        String msisdn = payload.getString("preferred_username");
        Account account = accountRepository.findFirstByMsisdn(msisdn);
        if (account != null) {
            callAuthServiceDto.setAccountId(account.getId());
            callAuthServiceDto.setMsisdn(account.getMsisdn());

            List<Function> activeFunctionsByAccount = functionRepository.getActiveFunctionsByAccount(account.getId());
            if (!CollectionUtils.isEmpty(activeFunctionsByAccount)) {
                Set<CustomAuthority> authorities = activeFunctionsByAccount.stream().map(function -> new CustomAuthority(function.getCode())).collect(Collectors.toSet());
                callAuthServiceDto.setCustomAuthorities(authorities);
            }
        }
        return callAuthServiceDto;
    }

    @Override
    public boolean checkPermission(String requestUri, HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization").substring(7);
        String msisdn = Util.getMsisdnFromJwt(bearerToken);
        logger.info("msisdn from bearerToken: " + msisdn);
        if (!msisdn.trim().isEmpty()) {
            Account account = accountRepository.findFirstByMsisdnAndStatus(msisdn, Status.ACTIVE);
            logger.info("account neeeeee: " + account);
            if (account != null) {
                List<Function> activePermissionsByAccount = functionRepository.getActiveFunctionsByAccount(account.getId());
                logger.info("activePermissionsByAccount neeeeee: " + account);
                if (!CollectionUtils.isEmpty(activePermissionsByAccount)) {
                    return checkEndpointsIsValid(activePermissionsByAccount, requestUri);
                }
            }
        }
        return false;
    }

    @Override
    public String getCurrentAccountId(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization").substring(7);
        String msisdn = Util.getMsisdnFromJwt(bearerToken);
        if (!msisdn.trim().isEmpty()) {
            return accountRepository.findIdByMsisdn(msisdn);
        } else {
            logger.error("[Error] cannot get msisdn from bearerToken: " + bearerToken);
            return "";
        }
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    private boolean checkEndpointsIsValid(List<Function> functions, String endpoint) {
        logger.info("functions and endpoint: " + functions + " " + endpoint);
        Pattern pattern = null;
        for (Function function : functions) {
            if(StringUtils.isEmpty(function.getEndPoints()))
                continue;

            String[] endpointsOfFunction = function.getEndPoints().split("\\s*,\\s*");
            for (String endpointPattern : endpointsOfFunction) {
                try {
                    if (pattern == null || !pattern.pattern().equals(endpointPattern)) {
                        pattern = Pattern.compile(endpointPattern);
                    }
                    if (pattern.matcher(endpoint).matches()) {
                        return true;
                    }
                } catch (PatternSyntaxException ex) {
                    logger.error(ExceptionUtils.getStackTrace(ex));
                    logger.debug("Pattern of function's endpoint is invalid.");
                }
            }
        }
        return false;

    }

}
