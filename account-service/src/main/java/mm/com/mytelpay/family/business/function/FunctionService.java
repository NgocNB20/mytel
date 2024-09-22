package mm.com.mytelpay.family.business.function;

import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.function.dto.*;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

public interface FunctionService {

    boolean addFunction(FunctionAddReqDTO request, HttpServletRequest httpServletRequest);

    boolean updateFunction(FunctionEditReqDTO request, HttpServletRequest httpServletRequest);

    boolean deleteFunction(SimpleRequest request, HttpServletRequest httpServletRequest);

    Page<FunctionFilterResDTO> getList(FunctionFilterReqDTO request, HttpServletRequest httpServletRequest);

    FunctionDetailDto getDetail(String id, HttpServletRequest httpServletRequest);

}
