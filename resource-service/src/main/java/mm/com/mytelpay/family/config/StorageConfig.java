package mm.com.mytelpay.family.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.Data;
import mm.com.mytelpay.family.utils.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("s3.configuration")
@Data
public class StorageConfig {

    @Value("${amazonProperties.accessKey}")
    private String accessKey;

    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Value("${amazonProperties.static}")
    private String region;

    @Value("${amazonProperties.basePath}")
    private String endPoint;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(Util.removeQuote(accessKey), Util.removeQuote(secretKey));

        AmazonS3 conn = new AmazonS3Client(credentials);
        conn.setEndpoint(Util.removeQuote(endPoint));

        return conn;
    }

}
