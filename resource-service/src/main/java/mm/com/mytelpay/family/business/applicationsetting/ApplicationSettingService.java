package mm.com.mytelpay.family.business.applicationsetting;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.applicationsetting.dto.*;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

public interface ApplicationSettingService {
    boolean create(AppSettingCreateReqDTO districtRequest, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean edit(AppSettingEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean delete (SimpleRequest request , HttpServletRequest httpServletRequest) throws JsonProcessingException;

    AppSettingDetailResDTO getDetail (SimpleRequest request, HttpServletRequest httpServletRequest);
    AppSettingDetailResDTO getListReason(AppSettingGetKeyDTO request, HttpServletRequest httpServletRequest);

    Page<AppSettingFilterResDTO> getList(AppSettingFilterReqDTO request, HttpServletRequest httpServletRequest);

    AppSettingDetailResDTO getByKey(AppSettingGetKeyDTO request, HttpServletRequest httpServletRequest);

    List<LocalDate> getPublicHolidaysInYear(Integer year);

}
