package mm.com.mytelpay.family.business.car;

import mm.com.mytelpay.family.business.scanqrhistory.dto.CarQRScanHistoryDetailResDTO;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterResDTO;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterReqDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.car.dto.*;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CarService {

    CarDetailResDTO getDetail(SimpleRequest request, HttpServletRequest httpServletRequest);

    Page<CarFilterResDTO> getList(CarFilterReqDTO request, HttpServletRequest httpServletRequest);

    boolean create(CarCreateReqDTO request, MultipartFile[] files, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean edit(CarEditReqDTO request, MultipartFile[] files, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    List<CarDetailResDTO> getListCarForAssign(CarAssignReqDTO request, HttpServletRequest httpServletRequest);

    List<CarReportDTO> getListCarReport (CarReportReqDTO carReportReqDTO, HttpServletRequest httpServletRequest);
    
    public CarQRDataResDTO validQRData(CarQRReqDTO qrString);
}
