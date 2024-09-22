package mm.com.mytelpay.family.business.common;

import mm.com.mytelpay.family.enums.FileAccess;
import mm.com.mytelpay.family.enums.ObjectType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
@Qualifier("NFS")
public class NFSServiceImpl implements StorageService {

    @Value("${app.local.file}")
    private String directory;
    Logger logger = LogManager.getLogger(NFSServiceImpl.class);

    @Override
    public String upload(MultipartFile multipartFile, ObjectType objectType, FileAccess fileAccess) throws IOException {
        String fileName = generateFileName(multipartFile);
        String dataDirectory = directory + "\\" + objectType + "\\";

        Path uploadPath = Paths.get(dataDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);

        Files.copy(multipartFile.getInputStream(), filePath);
        return FilenameUtils.concat(String.valueOf(objectType), fileName);
    }

    @Override
    public void delete(String fileName) {
        Path filePath = Paths.get(directory, fileName);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
                logger.info("Deleted file %s successfully", fileName);
            } catch (IOException e) {
                logger.error("Failed to delete file %s", fileName);
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    @Override
    public String getUrl() {
        return directory + "\\";
    }

    private String generateFileName(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
        return fileName.replace(" ", "") + "-" + new Date().getTime() + "." + extension;
    }
}
