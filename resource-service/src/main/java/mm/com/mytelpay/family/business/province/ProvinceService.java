package mm.com.mytelpay.family.business.province;

import mm.com.mytelpay.family.business.province.dto.*;
import mm.com.mytelpay.family.model.Province;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface ProvinceService {
    Province createProvince (ProvinceCreateReqDTO provinceCreateRequest , HttpServletRequest httpServletRequest);

    Province editProvince (ProvinceEditReqDTO request , HttpServletRequest httpServletRequest);

    void deleteProvince (SimpleRequest simpleRequest , HttpServletRequest httpServletRequest);

    ProvinceResDTO getDetail (SimpleRequest simpleRequest , HttpServletRequest httpServletRequest);

    Page<ProvinceFilterReqDTO> getList (ProvinceFilerResDTO resDTO, HttpServletRequest httpServletRequest);

    ResponseEntity<ByteArrayResource> importExcel (MultipartFile inputFile , HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> exportExcel(ProvinceFilerResDTO request, HttpServletRequest httpServletRequest) throws IOException;

    ResponseEntity<ByteArrayResource> exportExcelTemplate(HttpServletRequest httpServletRequest) throws IOException;
}
