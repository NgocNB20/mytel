package mm.com.mytelpay.family.business.resttemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.BaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.*;
import mm.com.mytelpay.family.business.bookingmeal.dto.UnitDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.UnitForChefDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.UnitForChefReqDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.*;
import mm.com.mytelpay.family.enums.BalanceActionType;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.utils.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    @Value("${external.get.device.url}")
    private String getDeviceApproveAccountIdAndRole;

    @Value("${external.get.account_report}")
    private String getAccountReport;

    @Value("${external.get.account}")
    private String getListAccountReport;

    @Value("${external.account.info}")
    private String getAccountInfoUrl;

    @Value("${external.account.listInfoByIds}")
    private String getListAccountsInfoUrl;

    @Value("${external.account.driverForAssign}")
    private String getListDriverForAssign;

    @Value("${app.secretKey}")
    String secretKeyStr;

    @Value("${external.account.update_balance}")
    private String updateBalance;

    @Value("${external.getDeviceByListAccount.url}")
    private String getDeviceByListAccountUrl;

    @Value("${external.unit.getList}")
    private String getListUnitUri;

    @Value("${external.unit.getUnitForChef}")
    private String getUnitForChef;

    public AccountDTO getAccountInfo(String accountId, String requestId, String bearerAuth) {
        AccountDTO account;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqEntity = new HttpEntity<>(new SimpleRequest(accountId, requestId), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getAccountInfoUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getAccountInfoUrl, commonResponse.getStatusCode(), reqEntity.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(requestId, BookingErrorCode.Account.NOT_FOUND, null);
            }
            account = mapper.map(Objects.requireNonNull(commonResponse.getBody()).getResult(), AccountDTO.class);
            if (ObjectUtils.isEmpty(account)) {
                throw new BusinessEx(requestId, BookingErrorCode.Account.NOT_FOUND, null);
            }
        } catch (BusinessEx ex){
            throw ex;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Account.NOT_FOUND, null);
        }
        return account;
    }

    public AccountDTO getAccountInfoOrNull(String accountId, String requestId, String bearerAuth) {
        AccountDTO account = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqEntity = new HttpEntity<>(new SimpleRequest(accountId, requestId), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getAccountInfoUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getAccountInfoUrl, commonResponse.getStatusCode(), reqEntity.getBody());
            if (!ObjectUtils.isEmpty(commonResponse)) {
                account = mapper.map(Objects.requireNonNull(commonResponse.getBody()).getResult(), AccountDTO.class);
            }
            return account;
        }
        catch (Exception e) {
            return null;
        }
    }

    public List<AccountDeviceResDTO> getAccountDeviceResDTO(String accountId, String roleId, RoleType type, String requestId, String bearerAuth) {
        List<AccountDeviceResDTO> account;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            headers.set(Constants.APP_SECRET_KEY, secretKeyStr);
            HttpEntity<AccountDeviceReqDTO> reqEntity;
            reqEntity = new HttpEntity<>(new AccountDeviceReqDTO(accountId, roleId, Objects.isNull(type) ? null : type.toString(), requestId), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getDeviceApproveAccountIdAndRole), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getDeviceApproveAccountIdAndRole, commonResponse.getStatusCode(), reqEntity.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                return Collections.emptyList();
            }
            account = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
            if (ObjectUtils.isEmpty(account)) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
        return account;
    }

    public List<DriverDTO> getListDriverAssign(Set<String> driverIds, String requestId, String bearerAuth) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<AssignIdsReqDTO> entity = new HttpEntity<>(
                    new AssignIdsReqDTO(new ArrayList<>(driverIds)), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    accountServiceBaseUrl.concat(getListDriverForAssign), HttpMethod.POST, entity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getListDriverForAssign, commonResponse.getStatusCode(), entity.getBody());
            return objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Account.NOT_FOUND, null);
        }
    }

    public void updateBalance(Integer amount, BalanceActionType actionType, String bearerAuth) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            headers.set(Constants.APP_SECRET_KEY, secretKeyStr);
            HttpEntity<UpdateBalanceDTO> reqEntity = new HttpEntity<>(new UpdateBalanceDTO(amount, actionType), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(updateBalance), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, updateBalance, commonResponse.getStatusCode(), reqEntity.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(BookingErrorCode.BookingMeal.NOT_ENOUGH_MONEY, null);
            }
            if (!Objects.requireNonNull(commonResponse.getBody()).getResult().equals(Boolean.TRUE)) {
                throw new BusinessEx(BookingErrorCode.BookingMeal.NOT_ENOUGH_MONEY, null);
            }
        }
        catch (Exception e) {
            throw new BusinessEx(BookingErrorCode.BookingMeal.NOT_ENOUGH_MONEY, null);
        }
    }

    public List<AccountDeviceResDTO> getListAccountDeviceRestDTO(List<String> accountIds) {
        List<AccountDeviceResDTO> account = new ArrayList<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set(Constants.APP_SECRET_KEY, secretKeyStr);
            HttpEntity<ListAccountsInfoReqDTO> reqEntity;
                reqEntity = new HttpEntity<>(new ListAccountsInfoReqDTO(accountIds), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getDeviceByListAccountUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getDeviceByListAccountUrl, commonResponse.getStatusCode(), reqEntity.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                return account;
            }
            account = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
            if (ObjectUtils.isEmpty(account)) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return account;
        }
        return account;
    }

    public List<AccountReportDTO> getListAccountReport(String requestId, List<String> id, String bearerAuth) {
        List<AccountReportDTO> accountReportDTOS = new ArrayList<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<AccountReportReqDTO> entity = new HttpEntity<>(new AccountReportReqDTO(id), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getListAccountReport), HttpMethod.POST, entity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getListAccountReport, commonResponse.getStatusCode(), entity.getBody());
            if (ObjectUtils.isEmpty(commonResponse) || ObjectUtils.isEmpty(Objects.requireNonNull(commonResponse.getBody()).getResult())) {
                throw new BusinessEx(requestId, BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
            }
            accountReportDTOS = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        } catch (Exception e){
            logger.error("--> Get list account report fail because {}", e.getMessage());
            return accountReportDTOS;
        }
        return accountReportDTOS;
    }

    public List<AccountReportDTO> listAccountReportDTO (String msisdn, String requestId , String bearerAuth){
        List<AccountReportDTO> accountReportDTOS;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<AccountReportMsisdnDTO> entity = new HttpEntity<>(new AccountReportMsisdnDTO(msisdn), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getAccountReport), HttpMethod.POST, entity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getAccountReport, commonResponse.getStatusCode(), entity.getBody());
            if (ObjectUtils.isEmpty(commonResponse) || ObjectUtils.isEmpty(Objects.requireNonNull(commonResponse.getBody()).getResult())) {
                throw new BusinessEx(requestId, BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
            }
            accountReportDTOS = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        }catch (Exception e){
            logger.error("--> Get list account report fail because {}", e.getMessage());
            throw new BusinessEx(requestId, BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
        }
        return accountReportDTOS;
    }

    public List<UnitDTO> getListUnit(String bearerAuth) {
        List<UnitDTO> unitDTOS = new ArrayList<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<UnitDTO> entity = new HttpEntity<>(new UnitDTO(), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getListUnitUri), HttpMethod.POST, entity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getListUnitUri, commonResponse.getStatusCode(), entity.getBody());
            Map<String, Object> result = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {});
            unitDTOS = objectMapper.convertValue(result.values().stream().findFirst().orElse(null), new TypeReference<>() {});

        } catch (Exception e) {
            logger.error("--> Get list unit fail because {}", e.getMessage());
        }
        return unitDTOS;
    }

    public UnitForChefDTO getUnitForChef(UnitForChefReqDTO reqDTO, String bearerAuth){
        UnitForChefDTO unitForChef;
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<UnitForChefReqDTO> entity = new HttpEntity<>(new UnitForChefReqDTO(reqDTO.getAccountId()), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getUnitForChef), HttpMethod.POST, entity, CommonResponseDTO.class);
            unitForChef = mapper.map(Objects.requireNonNull(commonResponse.getBody()).getResult(), UnitForChefDTO.class);
            if(ObjectUtils.isEmpty(unitForChef)){
                throw new BusinessEx(reqDTO.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
            }
        }catch (Exception e){
            logger.error("--> Get list unit fail because {}", e.getMessage());
            throw new BusinessEx(reqDTO.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
        }
        return unitForChef;
    }

    public List<AccInfoBasicDTO> getListAccountsInfo(List<String> accountIds, String requestId, String bearerAuth) {
        List<AccInfoBasicDTO> account;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set(Constants.APP_SECRET_KEY, secretKeyStr);
            headers.setBearerAuth(bearerAuth);
            HttpEntity<ListAccountsInfoReqDTO> reqEntity = new HttpEntity<>(new ListAccountsInfoReqDTO(accountIds), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(accountServiceBaseUrl.concat(getListAccountsInfoUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getListAccountsInfoUrl, commonResponse.getStatusCode(), reqEntity.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(requestId, BookingErrorCode.Account.NOT_FOUND, null);
            }
            account = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        } catch (BusinessEx ex){
            throw ex;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Account.NOT_FOUND, null);
        }
        return account;
    }
}