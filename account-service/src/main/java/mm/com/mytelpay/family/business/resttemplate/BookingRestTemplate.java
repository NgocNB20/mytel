package mm.com.mytelpay.family.business.resttemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.BaseBusiness;
import mm.com.mytelpay.family.business.resttemplate.dto.SendNoticeReqDTO;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

    @Value("${external.sendNotice.url}")
    private String sendNoticeUrl;

    public void sendFcmNoti(SendNoticeReqDTO sendNoticeReqDTO, String bearerAuth) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<SendNoticeReqDTO> reqEntity = new HttpEntity<>(sendNoticeReqDTO, headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(bookingServiceBaseUrl.concat(sendNoticeUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            logger.info("HTTP request sent. URL: {}, Response status: {}, Body: {}", sendNoticeReqDTO, commonResponse.getStatusCode(), reqEntity.getBody());
        }
        catch (Exception e) {
            logger.error("Send notice failed");
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

}