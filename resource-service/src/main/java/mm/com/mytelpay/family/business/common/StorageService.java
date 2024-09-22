package mm.com.mytelpay.family.business.common;

import mm.com.mytelpay.family.enums.FileAccess;
import mm.com.mytelpay.family.enums.ObjectType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String upload(MultipartFile multipartFile, ObjectType objectType, FileAccess fileAccess) throws IOException;

    void delete(String fileName);

    String getUrl();
}
