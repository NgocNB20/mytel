/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.scanqrhistory.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mm.com.mytelpay.family.utils.BasePagination;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarScanQRHistoryFilterReqDTO extends BasePagination{
    
    String userMsisdn;
    
    String from;
    
    String to;
}
