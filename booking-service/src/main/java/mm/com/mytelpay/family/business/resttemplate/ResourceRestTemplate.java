package mm.com.mytelpay.family.business.resttemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.BaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.*;
import mm.com.mytelpay.family.business.resttemplate.dto.HotelDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.*;
import mm.com.mytelpay.family.business.notification.NotificationGetKeyReqDTO;
import mm.com.mytelpay.family.business.notification.NotificationReqDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.*;
import mm.com.mytelpay.family.enums.Day;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.exception.BookingErrorCode;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.utils.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Component
public class ResourceRestTemplate extends BaseBusiness {


    @Autowired
    public ModelMapper mapper;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${external.resource.url}")
    private String resourceServiceBaseUrl;

    @Value("${external.car.report}")
    private String getListCarReport;

    @Value("${external.car.info}")
    private String getCarInfoUrl;

    @Value("${external.car.info.assign}")
    private String getCarAssignUrl;

    @Value("${external.meal.info}")
    private String getMealInfoUrl;

    @Value("${external.canteen.info}")
    private String getCanteenInfoUrl;

    @Value("${external.hotel.info}")
    private String getHotelInfoUrl;

    @Value("${external.hotel.getListByIds}")
    private String getListHotelByIds;

    @Value("${external.canteen.byIds}")
    private String getListCanteenByIds;

    @Value("${external.meal.getList}")
    private String getListMeal;

    @Value("${external.applicationSetting.get}")
    private String getApplicationByKey;

    @Value("${external.applicationSetting.getPublicHolidaysInYear}")
    private String getPublicHolidaysInYearUrl;

    @Value("${external.canteen.getCanteenForFChef}")
    private String getCanteenForChef;

    public CarDTO getCarInfo(String carId, String requestId, String bearerAuth) {
        CarDTO res;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqCar = new HttpEntity<>(
                    new SimpleRequest(carId, requestId),
                    headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    resourceServiceBaseUrl.concat(getCarInfoUrl),
                    HttpMethod.POST, reqCar, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getCarInfoUrl, commonResponse.getStatusCode(), reqCar.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(requestId, BookingErrorCode.Resource.CAR_NOT_FOUND, null);
            }
            res = mapper.map(Objects.requireNonNull(commonResponse.getBody()).getResult(), CarDTO.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Resource.CAR_NOT_FOUND, null);
        }
        return res;
    }

    public List<CarAssignDTO> getListCarAssign(Set<String> carIds, String requestId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(token);
            HttpEntity<AssignIdsReqDTO> entity = new HttpEntity<>(
                    new AssignIdsReqDTO(new ArrayList<>(carIds)), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    resourceServiceBaseUrl.concat(getCarAssignUrl), HttpMethod.POST, entity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getCarAssignUrl, commonResponse.getStatusCode(), entity.getBody());
            return objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Resource.CAR_NOT_FOUND, null);
        }
    }

    public HotelDTO getHotelInfo(String hotelId, String requestId, String bearerAuth) {
        HotelDTO res;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqHotel = new HttpEntity<>(
                    new SimpleRequest(hotelId, requestId),
                    headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    resourceServiceBaseUrl.concat(getHotelInfoUrl),
                    HttpMethod.POST, reqHotel, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getHotelInfoUrl, commonResponse.getStatusCode(), reqHotel.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(requestId, BookingErrorCode.Resource.HOTEL_NOT_FOUND, null);
            }
            res = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), HotelDTO.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Resource.HOTEL_NOT_FOUND, null);
        }
        return res;
    }

    public List<HotelDTO> getListHotelByIds(List<String> hotelIds, String requestId, String bearerAuth) {
        List<HotelDTO> res;
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<GetListByIds> reqHotel = new HttpEntity<>(
                    new GetListByIds(hotelIds),
                    headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    resourceServiceBaseUrl.concat(getListHotelByIds),
                    HttpMethod.POST, reqHotel, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getListHotelByIds, commonResponse.getStatusCode(), reqHotel.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(requestId, BookingErrorCode.Resource.HOTEL_NOT_FOUND, null);
            }
            res = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Resource.HOTEL_NOT_FOUND, null);
        }
        return res;
    }

    public CanteenResourceDTO getCanteenInfo(String canteenId, String requestId, String bearerAuth) {
        CanteenResourceDTO res;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqCanteen = new HttpEntity<>(
                    new SimpleRequest(canteenId ,requestId),
                    headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    resourceServiceBaseUrl.concat(getCanteenInfoUrl),
                    HttpMethod.POST, reqCanteen, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getCanteenInfoUrl, commonResponse.getStatusCode(), reqCanteen.getBody());
            res = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), CanteenResourceDTO.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new CanteenResourceDTO();
        }
        return res;
    }

    public List<CanteenResourceDTO> getListCanteenByIds(List<String> ids, String requestId, String bearerAuth) {
        List<CanteenResourceDTO> res;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<GetListByIds> reqCanteen = new HttpEntity<>(
                    new GetListByIds(ids),
                    headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    resourceServiceBaseUrl.concat(getListCanteenByIds),
                    HttpMethod.POST, reqCanteen, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getListCanteenByIds, commonResponse.getStatusCode(), reqCanteen.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(requestId, BookingErrorCode.Resource.CANTEEN_NOT_FOUND, null);
            }
            res = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Resource.CANTEEN_NOT_FOUND, null);
        }
        return res;
    }

    public List<GetListMenuResDTO> getListMeal(String canteenId, String requestId) {
        List<GetListMenuResDTO> res;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<GetListMenuReqDTO> reqCanteen = new HttpEntity<>(
                    new GetListMenuReqDTO(Day.ALL.toString(), canteenId, requestId),
                    headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    resourceServiceBaseUrl.concat(getListMeal),
                    HttpMethod.POST, reqCanteen, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getListMeal, commonResponse.getStatusCode(), reqCanteen.getBody());
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new BusinessEx(requestId, BookingErrorCode.Resource.MEAL_NOT_FOUND, null);
            }
            Map<String, Object> result = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {});
            res = objectMapper.convertValue(result.values().stream().findFirst().orElse(null), new TypeReference<>() {});
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, BookingErrorCode.Resource.MEAL_NOT_FOUND, null);
        }
        return res;
    }

    public Map<MealType, Integer> getPriceMeal(String key, String requestId, String bearerAuth) {
        EnumMap<MealType, Integer> mapPrice = new EnumMap<>(MealType.class);
        try {
            List<PriceMealDTO> priceMealDTOS;
            NotificationReqDTO noticeDTO = getApplicationByKey(key, requestId, bearerAuth);
            priceMealDTOS = Arrays.asList(objectMapper.readValue(noticeDTO.getValue(), PriceMealDTO[].class));
            priceMealDTOS.forEach(p -> mapPrice.put(p.getMealType(), p.getPrice()));
        } catch (Exception e){
            logger.error("Get list price meal fail fail, ", e);
            throw new BusinessEx(CommonException.Business.SYSTEM_BUSY, null);
        }
        return mapPrice;
    }

    public NotificationReqDTO getApplicationByKey(String key, String requestId, String bearerAuth) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(bearerAuth);
        HttpEntity<NotificationGetKeyReqDTO> reqEntity = new HttpEntity<>(new NotificationGetKeyReqDTO(key, requestId), headers);
        ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(resourceServiceBaseUrl.concat(getApplicationByKey), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
        logger.info(Constants.LOG_INFO_REST_TEMPLATE, getApplicationByKey, commonResponse.getStatusCode(), reqEntity.getBody());
        NotificationReqDTO noticeDTO = new NotificationReqDTO();
        if (!ObjectUtils.isEmpty(commonResponse)) {
            noticeDTO = mapper.map(Objects.requireNonNull(commonResponse.getBody()).getResult(), NotificationReqDTO.class);
        }
        return noticeDTO;
    }

    public GetMealByIdDTO getMealDetailById(String mealId, String requestId, String bearerAuth) {
        GetMealByIdDTO res = new GetMealByIdDTO();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> req = new HttpEntity<>(
                    new SimpleRequest(mealId, requestId),
                    headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(
                    resourceServiceBaseUrl.concat(getMealInfoUrl),
                    HttpMethod.POST, req, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getMealInfoUrl, commonResponse.getStatusCode(), req.getBody());

            if (!ObjectUtils.isEmpty(commonResponse)) {
                res = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), GetMealByIdDTO.class);
            }
        } catch (Exception e){
            logger.error("Get meal detail fail, ", e);
            return new GetMealByIdDTO();
        }
        return res;
    }

    public List<LocalDate> getPublicHolidaysInYear(Integer year, String bearerAuth) {
        List<LocalDate> publicHolidaysInYear;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            String url = resourceServiceBaseUrl.concat(getPublicHolidaysInYearUrl) + "?year=" + year;
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, url, commonResponse.getStatusCode(), requestEntity.getBody());
            publicHolidaysInYear = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        } catch (Exception e){
            logger.error("Get public holidays in year fail, ", e);
            return new ArrayList<>();
        }
        return publicHolidaysInYear;
    }

    public List<CarReportDTO> carReportDTOS (List<String> carId, String bearerAuth) {
        List<CarReportDTO> carReportDTOS;
        try {
            if (CollectionUtils.isEmpty(carId)) {
                carId = null;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<CarRportReqDTO> entity = new HttpEntity<>(new CarRportReqDTO(carId), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(resourceServiceBaseUrl.concat(getListCarReport), HttpMethod.POST, entity, CommonResponseDTO.class);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, getListCarReport, commonResponse.getStatusCode(), entity.getBody());
            carReportDTOS = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), new TypeReference<>() {
            });
        } catch (Exception e){
            logger.error("Get list car report fail, ", e);
            return new ArrayList<>();
        }
        return carReportDTOS;
    }

    public CanteenForChefDTO getCanteenForChef(CanteenForChefReqDTO reqDTO, String bearerAuth){
        CanteenForChefDTO canteenForChefDTO;
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<CanteenForChefReqDTO> entity = new HttpEntity<>(new CanteenForChefReqDTO(reqDTO.getUnitId()), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(resourceServiceBaseUrl.concat(getCanteenForChef), HttpMethod.POST, entity, CommonResponseDTO.class);
            canteenForChefDTO = mapper.map(Objects.requireNonNull(commonResponse.getBody()).getResult(), CanteenForChefDTO.class);
            if(ObjectUtils.isEmpty(canteenForChefDTO)){
                throw new BusinessEx(reqDTO.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
            }
        }
        catch (Exception e){
            logger.error("Get canteen for chef fail, ", e);
            throw new BusinessEx(reqDTO.getRequestId(), BookingErrorCode.BookingCar.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS, null);
        }
        return canteenForChefDTO;
    }

}
