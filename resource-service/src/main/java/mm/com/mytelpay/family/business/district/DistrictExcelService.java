package mm.com.mytelpay.family.business.district;

import mm.com.mytelpay.family.business.district.dto.DistrictFilterResDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface DistrictExcelService {
    ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> exportExcel(DistrictFilterResDTO request, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> downloadTemplate(HttpServletRequest httpServletRequest) throws IOException;
}
