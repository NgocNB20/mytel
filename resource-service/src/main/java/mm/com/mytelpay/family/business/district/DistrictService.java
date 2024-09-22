package mm.com.mytelpay.family.business.district;

import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.district.dto.*;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

public interface DistrictService {
    boolean create(DistrictReqDTO districtRequest, HttpServletRequest httpServletRequest);

    boolean edit(DistrictEditReqDTO request, HttpServletRequest httpServletRequest);

    boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest);

    DistrictResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest);

    Page<DistrictFilterReqDTO> getList(DistrictFilterResDTO request, HttpServletRequest httpServletRequest);
}
