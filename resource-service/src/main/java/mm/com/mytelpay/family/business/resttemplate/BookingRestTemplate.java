package mm.com.mytelpay.family.business.resttemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.BaseBusiness;
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

@Component
public class BookingRestTemplate extends BaseBusiness {

    @Autowired
    public ModelMapper mapper;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${external.booking.url}")
    private String bookingServiceBaseUrl;

    @Value("${external.booking.checkBookedHotel}")
    private String checkBookedHotelUrl;

    public Boolean checkExistedBookedHotel(String hotelId, String requestId, String bearerAuth) {
        Boolean checkBookedHotel;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SimpleRequest> reqEntity = new HttpEntity<>(new SimpleRequest(hotelId, requestId), headers);

            ResponseEntity<CommonResponseDTO> checkBookedHotelRes = restTemplate.exchange(bookingServiceBaseUrl.concat(checkBookedHotelUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            if (ObjectUtils.isEmpty(checkBookedHotelRes)) {
                throw new BusinessEx(requestId, ResourceErrorCode.Hotel.HOTEL_BOOKED_CANNOT_BE_DELETED, null);
            }
            checkBookedHotel = mapper.map(Objects.requireNonNull(checkBookedHotelRes.getBody()).getResult(), Boolean.class);
        } catch (BusinessEx ex) {
            throw ex;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, ResourceErrorCode.Account.NOT_FOUND, null);
        }
        return checkBookedHotel;
    }

}
