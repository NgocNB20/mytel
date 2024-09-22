package mm.com.mytelpay.family.business.hotel;

import mm.com.mytelpay.family.business.hotel.dto.HotelExportReqDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface HotelExcelService {
    ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> exportExcel(HotelExportReqDTO request, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> exportTemplate(HttpServletRequest httpServletRequest) throws IOException;

}
