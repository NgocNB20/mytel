package mm.com.mytelpay.family.business.car.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.business.file.FileResponse;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDetailResDTO {

    private String id;

    private String name;

    private CarType carType;

    private String model;

    private String licensePlate;

    private Status status;

    private LocalDateTime createdAt;

    private List<FileResponse> file; 
    
    private String qrString;
}
