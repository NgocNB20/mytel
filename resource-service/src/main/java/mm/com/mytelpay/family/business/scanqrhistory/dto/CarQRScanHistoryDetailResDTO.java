/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.scanqrhistory.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.enums.CarType;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarQRScanHistoryDetailResDTO {

    private String id;
     
    String userId;
    
    String userName;
    
    String userEmail;
    
    private String userMsisdn;
    
    private String carId;
    
    private String carName;
    
    private CarType carType;
    
    private String carLicensePlate;
    
    private LocalDateTime createdAt;
}
