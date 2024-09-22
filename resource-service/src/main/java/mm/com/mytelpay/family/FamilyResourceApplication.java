package mm.com.mytelpay.family;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.TimeZone;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableWebSecurity
@EnableCaching
public class FamilyResourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FamilyResourceApplication.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Yangon"));
    }
}
