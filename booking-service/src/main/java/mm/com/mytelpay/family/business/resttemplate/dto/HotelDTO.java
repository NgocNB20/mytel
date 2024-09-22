package mm.com.mytelpay.family.business.resttemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {

    private String id;

    private String name;

    private String phone;

    private String address;

    private Integer rating;

    private String description;

    private Integer maxPrice;

    private Integer maxPlusPrice;

    private List<String> rolesAllow;

    private List<FileResponse> file;
}
