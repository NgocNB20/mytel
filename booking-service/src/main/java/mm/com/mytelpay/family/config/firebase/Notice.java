package mm.com.mytelpay.family.config.firebase;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class Notice implements Serializable {

    private String subject;

    private String content;

    private String image;

    private Map<String, String> data;

    private List<String> registrationTokens;

    public Notice(String subject, String content, String image, Map<String, String> data, List<String> registrationTokens) {
        this.subject = subject;
        this.content = content;
        this.image = image;
        this.data = data;
        this.registrationTokens = registrationTokens;
    }
}