package mm.com.mytelpay.family.business.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelDetailResDTO {

    private String code;

    private String name;

    private String phone;

    private String address;

    private List<String> rolesAllow;

    private Integer rating;

    private String districtId;

    private String districtCode;

    private String districtName;

    private String provinceId;

    private String provinceCode;

    private String provinceName;

    private Integer maxPrice;

    private Integer maxPlusPrice;

    private List<FileResponse> file;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;

    private String description;
}
