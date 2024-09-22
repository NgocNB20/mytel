package mm.com.mytelpay.family.business.resttemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.BaseBusiness;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountSettingDTO;
import org.springframework.web.client.RestClientException;

@Component
public class AccountRestTemplate extends BaseBusiness {

    @Autowired
    public ModelMapper mapper;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${external.account.url}")
    private String accountServiceBaseUrl;

    @Value("${external.account.info}")
    private String getAccountInfoUrl;
    
    @Value("${external.account.setting}")
    private String getAccountSettingUrl;

    @Value("${app.secretKey}")
    String secretKeyStr;

    public AccountDTO getAccountInfo(String accountId, String requestId, String bearerAuth) {
        AccountDTO account;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqEntity = new HttpEntity<>(new SimpleRequest(accountId, requestId), headers);

            ResponseEntity<CommonResponseDTO> accountRes = restTemplate.exchange(accountServiceBaseUrl.concat(getAccountInfoUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            if (ObjectUtils.isEmpty(accountRes)) {
                throw new BusinessEx(requestId, ResourceErrorCode.Account.NOT_FOUND, null);
            }
            account = mapper.map(Objects.requireNonNull(accountRes.getBody()).getResult(), AccountDTO.class);
            if (ObjectUtils.isEmpty(account)) {
                throw new BusinessEx(requestId, ResourceErrorCode.Account.NOT_FOUND, null);
            }
        } catch (BusinessEx ex) {
            throw ex;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, ResourceErrorCode.Account.NOT_FOUND, null);
        }
        return account;
    }
    
    public AccountSettingDTO getAccountSettings(String accountId, String requestId, String bearerAuth){
        AccountSettingDTO accountConfig;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqEntity = new HttpEntity<>(new SimpleRequest(accountId, requestId), headers);

            ResponseEntity<CommonResponseDTO> accountConfigRes = restTemplate.exchange(accountServiceBaseUrl.concat(getAccountSettingUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            if (ObjectUtils.isEmpty(accountConfigRes)) {
                throw new BusinessEx(requestId, ResourceErrorCode.Account.NOT_FOUND, null);
            }
            accountConfig = mapper.map(Objects.requireNonNull(accountConfigRes.getBody()).getResult(), AccountSettingDTO.class);
            if (ObjectUtils.isEmpty(accountConfig)) {
                throw new BusinessEx(requestId, ResourceErrorCode.Account.NOT_FOUND, null);
            }
        } catch (BusinessEx ex) {
            throw ex;
        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, ResourceErrorCode.Account.NOT_FOUND, null);
        }
        
        return accountConfig;
    }
}
