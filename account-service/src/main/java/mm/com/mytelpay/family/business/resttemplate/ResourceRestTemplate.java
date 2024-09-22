package mm.com.mytelpay.family.business.resttemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import mm.com.mytelpay.family.business.BaseBusiness;
import mm.com.mytelpay.family.business.resttemplate.dto.CheckCanteenForChefReqDTO;
import mm.com.mytelpay.family.business.resttemplate.dto.CheckCanteenForChefResDTO;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
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
public class ResourceRestTemplate extends BaseBusiness {
    @Autowired
    public ModelMapper mapper;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${external.resource.url}")
    private String resourceServiceBaseUrl;

    @Value("${external.canteen.checkCanteenForChef}")
    private String checkCanteenForChef;

    public CheckCanteenForChefResDTO checkCanteenForChef (String canteenId, String requestId, String bearerAuth) {
        CheckCanteenForChefResDTO res;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(bearerAuth);
            HttpEntity<CheckCanteenForChefReqDTO> entity = new HttpEntity<>(new CheckCanteenForChefReqDTO(canteenId), headers);
            ResponseEntity<CommonResponseDTO> commonResponse = restTemplate.exchange(resourceServiceBaseUrl.concat(checkCanteenForChef), HttpMethod.POST, entity, CommonResponseDTO.class);
            res = objectMapper.convertValue(Objects.requireNonNull(commonResponse.getBody()).getResult(), CheckCanteenForChefResDTO.class);
            if (ObjectUtils.isEmpty(res)) {
                throw new BusinessEx(requestId, AccountErrorCode.Canteen.CANTEEN_NOT_FOUND, null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessEx(requestId, AccountErrorCode.Canteen.CANTEEN_NOT_FOUND, null);
        }
        return res;
    }
}
