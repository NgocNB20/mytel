package mm.com.mytelpay.family.business.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.food.dto.*;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface FoodService {

    Page<FoodFilterResDTO> filter(FoodFilterReqDTO request, HttpServletRequest httpServletRequest);

    FoodDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean create(FoodCreateArrayReqDTO reqDTO, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean edit(FoodEditReqDTO request, MultipartFile file, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

}
