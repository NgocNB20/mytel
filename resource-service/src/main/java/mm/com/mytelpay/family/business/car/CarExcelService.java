package mm.com.mytelpay.family.business.car;

import mm.com.mytelpay.family.business.car.dto.CarExportReqDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface CarExcelService {
    ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> exportExcel(CarExportReqDTO carExportReqDTO, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> downloadTemplate (HttpServletRequest httpServletRequest) throws IOException;
}
