package mm.com.mytelpay.family.business.canteen;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.canteen.dto.*;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CanteenService {

    Page<CanteenFilterResDTO> filter(CanteenFilterReqDTO request, HttpServletRequest httpServletRequest);

    CanteenDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean create(CanteenCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean edit(CanteenEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    List<CanteenFilterResDTO> getListByIds(CanteenGetIdsResDTO canteenGetIdsResDTO, HttpServletRequest httpServletRequest);

    CanteenForChefDTO getCanteenForChef (CanteenForChefReqDTO reqDTO, HttpServletRequest httpServletRequest);

    checkCanteenForChefResDTO checkCanteenId (CheckCanteenForChefReqDTO reqDTO, HttpServletRequest httpServletRequest);

    List<GetAllCanteenResDTO> getAllCanteen(HttpServletRequest httpServletRequest);
}
