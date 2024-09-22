package mm.com.mytelpay.family.business.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelFilterResDTO {

    private String id;

    private String code;

    private String name;

    private String provinceId;

    private String provinceCode;

    private String provinceName;

    private String districtId;

    private String districtCode;

    private String districtName;

    private String phone;

    private String address;

    private Integer rating;

    private String description;

    private List<FileResponse> file;

    public HotelFilterResDTO(String id, String name, String phone, String address, Integer rating, String description) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.rating = rating;
        this.description = description;
    }
}
