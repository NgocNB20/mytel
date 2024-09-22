package mm.com.mytelpay.family.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.exception.validate.NotBlank;
import mm.com.mytelpay.family.exception.validate.SizeRegex;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleRequest extends BaseRequest {

    @NotBlank
    @SizeRegex(max = 100)
    private String id;

    public SimpleRequest(String id, String requestId) {
        this.id = id;
        this.requestId = requestId;
    }

    public SimpleRequest(String requestId, String currentTime, String id) {
        this.id = id;
        this.requestId = requestId;
        this.currentTime = currentTime;
    }
}
