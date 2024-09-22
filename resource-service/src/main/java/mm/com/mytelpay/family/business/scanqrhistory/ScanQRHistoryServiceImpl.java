/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.scanqrhistory;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import mm.com.mytelpay.family.business.FamilyBaseBusiness;
import mm.com.mytelpay.family.business.car.dto.CarQRDataResDTO;
import mm.com.mytelpay.family.business.car.dto.CarQRReqDTO;
import mm.com.mytelpay.family.business.resttemplate.AccountRestTemplate;
import mm.com.mytelpay.family.business.resttemplate.dto.AccountDTO;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarQRScanHistoryDetailResDTO;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterReqDTO;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterResDTO;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.CarScanQRSHistory;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repository.CarScanQRHistoryRepository;
import mm.com.mytelpay.family.utils.ObjectMapperUtil;
import static mm.com.mytelpay.family.utils.PageableUtils.pageable;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Log4j2
@AllArgsConstructor
@Service
public class ScanQRHistoryServiceImpl extends FamilyBaseBusiness implements ScanQRHistoryService {
    
    public ScanQRHistoryServiceImpl() {
        logger = LogManager.getLogger(ScanQRHistoryServiceImpl.class);
    }
    @Autowired
    CarScanQRHistoryRepository carScanQRHistoryRepository;
    
    @Autowired
    private AccountRestTemplate accountRestTemplate;
    
    @Override
    public Page<CarScanQRHistoryFilterResDTO> getScanQRHistoryList(CarScanQRHistoryFilterReqDTO request, HttpServletRequest httpServletRequest) {
        Pageable pageable = pageable(request.getPageIndex(), request.getPageSize(), request.getSortBy(), request.getSortOrder());
        
        LocalDateTime fromTime = StringUtils.isBlank(request.getFrom()) ? null : Util.convertToLocalDateTimeStartOfDay(request.getFrom());
        LocalDateTime toTime = StringUtils.isBlank(request.getTo()) ? null : Util.convertToLocalDateTimeEndOfDay(request.getTo());
        
        Page<CarScanQRHistoryFilterResDTO> responses = carScanQRHistoryRepository.filterCarScanQRHistory(
                StringUtils.isBlank(request.getUserMsisdn()) ? null : request.getUserMsisdn(),
                fromTime,
                toTime,
                pageable);
        
        return new PageImpl<>(responses.getContent(), pageable, responses.getTotalElements());
    }
    
    @SneakyThrows
    @Override
    public CarQRScanHistoryDetailResDTO getQRScanHistoryDetail(SimpleRequest request, HttpServletRequest httpServletRequest) {
        CarScanQRSHistory history = carScanQRHistoryRepository.getCarScanQRHistoryById(request.getId()).orElseThrow(() -> {
            logger.error("Car Scan QR Code History id:{} not found", request.getId());
            throw new BusinessEx(request.getId(), ResourceErrorCode.Car.QR_SCAN_HISTORY_NOT_FOUND, null);
        });    

        CarQRScanHistoryDetailResDTO response = ObjectMapperUtil.convert(history, CarQRScanHistoryDetailResDTO.class);
        return response;
    }
    
    
    @Override
    public void saveQRScanHistory(CarQRReqDTO request, CarQRDataResDTO carInfo){
        
        AccountDTO accountInfo = accountRestTemplate.getAccountInfo(perRequestContextDto.getCurrentAccountId(), request.getRequestId(), perRequestContextDto.getBearToken());


        CarScanQRSHistory history = CarScanQRSHistory.builder()
                .userId(accountInfo.getAccountId())
                .userName(accountInfo.getName())
                .userEmail(accountInfo.getEmail())
                .userMsisdn(accountInfo.getMsisdn())
                .carId(carInfo.getId())
                .carName(carInfo.getName())
                .carLicensePlate(carInfo.getLicensePlate())
                .carType(carInfo.getCarType())
                .build();
        
        carScanQRHistoryRepository.save(history);
    }
}
