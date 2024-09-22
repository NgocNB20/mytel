package mm.com.mytelpay.family;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

import java.util.TimeZone;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableCaching
public class FamilyAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(FamilyAccountApplication.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Yangon"));
    }
}
