package mm.com.mytelpay.family.business.hotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.hotel.dto.*;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface HotelService {

    boolean createHotel(HotelCreateReqDTO chefCreateRequest, MultipartFile[] files, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    ResponseEntity<CommonResponseDTO> getListForCms(HotelFilterReqDTO request, HttpServletRequest httpServletRequest);

    ResponseEntity<CommonResponseDTO> getListForApp(HotelFilterReqDTO request, HttpServletRequest httpServletRequest);

    List<HotelFilterResDTO> getHotelsByIds(GetHotelsByIdsReqDTO request, HttpServletRequest httpServletRequest);

    HotelDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean editHotel(HotelEditReqDTO request, MultipartFile[] files, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    void deleteHotel(SimpleRequest request, HttpServletRequest httpServletRequest);

}
