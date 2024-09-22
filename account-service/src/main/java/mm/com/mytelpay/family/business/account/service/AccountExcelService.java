package mm.com.mytelpay.family.business.account.service;

import mm.com.mytelpay.family.business.account.dto.AccountExportDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AccountExcelService {
    ResponseEntity<ByteArrayResource> importExcel(MultipartFile file);

    ResponseEntity<ByteArrayResource> exportExcel(AccountExportDTO accountExportDTO);

    ResponseEntity<ByteArrayResource> downloadTemplate();

    void sendSmsNotifyAfterAddNewUser(String requestId, String phoneOfUser, String password, String message);

}
