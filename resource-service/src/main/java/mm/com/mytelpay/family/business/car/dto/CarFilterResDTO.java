package mm.com.mytelpay.family.business.car.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.CarType;
import mm.com.mytelpay.family.enums.Status;

import java.time.LocalDateTime;
import mm.com.mytelpay.family.utils.AESencryptionUtil;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarFilterResDTO {

    private String id;

    private String name;

    private CarType carType;

    private String licensePlate;

    private String model;

    private Status status;

    private LocalDateTime createdAt;
    
    private String qrString;

    public CarFilterResDTO(String id, String name, CarType carType, String licensePlate, String model, Status status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.carType = carType;
        this.licensePlate = licensePlate;
        this.model = model;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    
    public void generateQRString(){
        CarQRDataResDTO qrData = CarQRDataResDTO.builder()
                        .id(this.id)
                        .name(this.name)
                        .carType(this.carType)
                        .licensePlate(this.licensePlate)
                        .build();
        
       qrString = AESencryptionUtil.encryptAES(qrData);
    }
}
