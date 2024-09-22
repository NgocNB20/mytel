package mm.com.mytelpay.family.business.notification;

import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class ApplicationSettingCommonService {

    @Value("${external.applicationSetting.get}")
    private String getNoticeByKeyUrl;

    @Value("${external.resource.url}")
    private String resourceServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public ModelMapper mapper;

    public NotificationReqDTO getNoticeByKey(String key, String requestId, String bearerAuth) {
        NotificationReqDTO noticeDTO = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<NotificationGetKeyReqDTO> reqEntity = new HttpEntity<>(new NotificationGetKeyReqDTO(key, requestId), headers);

            ResponseEntity<CommonResponseDTO> noticeDTORes = restTemplate.exchange(resourceServiceBaseUrl.concat(getNoticeByKeyUrl), HttpMethod.POST, reqEntity, CommonResponseDTO.class);
            if (!ObjectUtils.isEmpty(noticeDTORes)) {
                noticeDTO = mapper.map(Objects.requireNonNull(noticeDTORes.getBody()).getResult(), NotificationReqDTO.class);
            }
        } catch (Exception e) {
            return null;
        }
        return noticeDTO;
    }

    public String getMessageByKey(String key, String requestId, String bearerAuth, String defaultValue) {
        NotificationReqDTO noticeDTO = this.getNoticeByKey(key, requestId, bearerAuth);
        return noticeDTO == null ? defaultValue : noticeDTO.getValue();
    }

}
