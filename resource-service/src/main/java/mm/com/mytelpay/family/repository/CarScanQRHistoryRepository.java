/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterResDTO;
import mm.com.mytelpay.family.model.CarScanQRSHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarScanQRHistoryRepository extends JpaRepository<CarScanQRSHistory, String>{
    
    @Query(value = "SELECT new mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterResDTO(c.id, c.userName, c.userMsisdn, c.carLicensePlate, c.createdAt )" +
            " FROM CarScanQRSHistory c "
            + "WHERE (:userMsisdn IS NULL OR c.userMsisdn like CONCAT('%', :userMsisdn,'%'))"
            + "AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d %H:%i:%s') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d %H:%i:%s')))"
            + "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d %H:%i:%s') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d %H:%i:%s')))")
    Page<CarScanQRHistoryFilterResDTO> filterCarScanQRHistory(String userMsisdn, LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable);
    
    @Query(value = "SELECT c FROM CarScanQRSHistory c WHERE c.id = :id")
    Optional<CarScanQRSHistory> getCarScanQRHistoryById(String id);
}
