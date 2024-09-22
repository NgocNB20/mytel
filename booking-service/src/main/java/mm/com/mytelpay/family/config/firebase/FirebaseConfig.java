package mm.com.mytelpay.family.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    @Value("${fire-base-config-file}")
    private String pathFirebaseConfig;

    @Bean
    @SneakyThrows
    FirebaseMessaging firebaseMessaging() {
        File file = ResourceUtils.getFile(pathFirebaseConfig);
        InputStream in = new FileInputStream(file);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(in))
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);

        return FirebaseMessaging.getInstance(app);

    }
}