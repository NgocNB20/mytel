/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.scanqrhistory;

import javax.servlet.http.HttpServletRequest;
import mm.com.mytelpay.family.business.car.dto.CarQRDataResDTO;
import mm.com.mytelpay.family.business.car.dto.CarQRReqDTO;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarQRScanHistoryDetailResDTO;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterReqDTO;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterResDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.Page;

public interface ScanQRHistoryService {
        
    Page<CarScanQRHistoryFilterResDTO> getScanQRHistoryList(CarScanQRHistoryFilterReqDTO request, HttpServletRequest httpServletRequest);
    
    CarQRScanHistoryDetailResDTO getQRScanHistoryDetail(SimpleRequest request, HttpServletRequest httpServletRequest);
    
    void saveQRScanHistory(CarQRReqDTO request, CarQRDataResDTO carInfo);
}
