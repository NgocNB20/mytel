/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.scanqrhistory.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarScanQRHistoryFilterResDTO {
    String id;
    
    String userName;
    
    String userMsisdn;

    String carLicensePlate;
    
    LocalDateTime createdAt;
}
