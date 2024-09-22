package mm.com.mytelpay.family.business.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import mm.com.mytelpay.family.enums.FileAccess;
import mm.com.mytelpay.family.enums.ObjectType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Date;

import static mm.com.mytelpay.family.utils.StringUtils.removeQuote;

@Service
@Qualifier("S3")
public class S3ServiceImpl implements StorageService {

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3client;

    @Value("${amazonProperties.basePathPublic}")
    public String urlPubic;

    @Value("${amazonProperties.basePath}")
    public String url;

    @Override
    public String upload(MultipartFile multipartFile, ObjectType objectType, FileAccess fileAccess) throws IOException {
        String fileName = FilenameUtils.concat(String.valueOf(objectType), generateFileName(multipartFile));
        String fileNameUpload = FilenameUtils.separatorsToUnix(fileName);
        String contentType = URLConnection.guessContentTypeFromName(multipartFile.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(multipartFile.getSize());
        if (fileAccess.equals(FileAccess.PUBLIC)) {
            s3client.putObject(new PutObjectRequest((removeQuote(bucketName)), fileNameUpload, multipartFile.getInputStream(), metadata).withCannedAcl(CannedAccessControlList.PublicRead));
        } else {
            s3client.putObject(removeQuote(bucketName), fileNameUpload, multipartFile.getInputStream(), metadata);
        }
        return fileNameUpload;
    }

    @Override
    public void delete(String fileName) {
        if (fileName != null ){
            s3client.deleteObject(removeQuote(bucketName), fileName);
        }
    }

    @Override
    public String getUrl() {
        return urlPubic + "/" + bucketName + "/" ;
    }

    private String generateFileName(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
        return fileName.replace(" ", "") + "-" + new Date().getTime() + "." + extension;
    }
}
