package mm.com.mytelpay.family.business.canteen;

import mm.com.mytelpay.family.business.canteen.dto.CanteenFilterReqDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface CanteenExcelService {
    ResponseEntity<ByteArrayResource> importExcel(MultipartFile inputFile, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> exportExcel(CanteenFilterReqDTO request, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> downloadTemplate (HttpServletRequest httpServletRequest) throws IOException;
}
