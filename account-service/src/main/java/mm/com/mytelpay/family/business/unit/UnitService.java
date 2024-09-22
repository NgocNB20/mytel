package mm.com.mytelpay.family.business.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.unit.dto.*;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UnitService {

    Page<UnitFilterResDTO> filter(UnitFilterReqDTO request, HttpServletRequest httpServletRequest);

    List<UnitFilterResDTO> getAll(HttpServletRequest httpServletRequest);

    UnitDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean create(UnitCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean edit(UnitEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest);

    UnitForCanteenDTO isUnitIdExisted(UnitForCanteenReqDTO reqDTO);

    List<UnitForCanteenDTO> getListUnit (UnitInfoReqDTO reqDTO);

    UnitForChefDTO getUnitForChef (UnitForChefReqDTO reqDTO);
}
