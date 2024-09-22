package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.bookingcar.dto.*;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.enums.CarBookingType;
import mm.com.mytelpay.family.model.BookingCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingCarRepository extends JpaRepository<BookingCar, String> {

    @Query(value = "SELECT c FROM BookingCar c WHERE c.id = :id ")
    Optional<BookingCar> findByBookingId(String id);
    @Query("SELECT bc FROM BookingCar bc WHERE bc.timeStart <= :startDate AND (bc.bookingStatus = 'PENDING' OR bc.bookingStatus = 'APPROVING')")
    List<BookingCar> findBookingCarExpired(LocalDateTime startDate);

    @Query(value = "SELECT c FROM BookingCar c WHERE c.id = :id AND c.bookingStatus = :bookingStatus")
    Optional<BookingCar> findByIdAndBookingStatus(String id, BookingStatus bookingStatus);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingcar.dto.FilterBookingCarResDTO(" +
            "c.id, c.typeBooking," +
            "c.original, c.destination," +
            "c.timeStart, " +
            "c.timeReturn, " +
            " c.bookingStatus, c.quantity," +
            " c.createdAt " +
            " )" +
            " FROM BookingCar c " +
            "WHERE (:bookingStatus IS NULL OR c.bookingStatus = :bookingStatus)" +
            "AND (c.accountId = :id)" +
            "AND (:typeBooking IS NULL OR c.typeBooking = :typeBooking)" +
            "AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            " " +
            "")
    Page<FilterBookingCarResDTO> filterBookingCar(@Param("typeBooking") CarBookingType typeBooking,
                                                  @Param("bookingStatus") BookingStatus bookingStatus,
                                                  String id,
                                                  LocalDateTime fromTime,
                                                  LocalDateTime toTime,
                                                  Pageable pageable);


    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingcar.dto.GetListReqBookingCarResDTO(" +
            "c.id," +
            " c.typeBooking," +
            " c.original, " +
            " c.destination," +
            " c.timeStart, " +
            " c.timeReturn, " +
            " c.bookingStatus, " +
            " c.createdAt, " +
            " c.accountId," +
            " c.approveLevel, " +
            " c.quantity, " +
            " c.distance " +
            " )" +
            " FROM BookingCar c " +
            "WHERE (:bookingStatus IS NULL OR c.bookingStatus = :bookingStatus)" +
            "AND (c.accountId = :accountId)" +
            "AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            " " +
            "")
    Page<GetListReqBookingCarResDTO> getListBookingCarForEU(
            @Param("bookingStatus") BookingStatus bookingStatus,
            LocalDateTime fromTime, LocalDateTime toTime, String accountId,
            Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingcar.dto.ListBookingCarReportDTO(" +
            "c.id , c.typeBooking ,c.original , c.destination, c.quantity, c.bookingStatus , c.createdAt, c.accountId )" +
            " FROM BookingCar c inner join BookingCarDetail cd ON c.id = cd.bookingCarId" +
            " where (:bookingStatus IS NULL OR c.bookingStatus = :bookingStatus)" +
            " AND (:driverId IS NULL OR cd.driverId = :driverId)" +
            " AND (:bookingType IS NULL OR c.typeBooking = :bookingType) " +
            " AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            " AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            " AND (coalesce(:accountId) IS NULL OR c.accountId IN (:accountId))" +
            " GROUP BY c.id" +
            " ORDER BY c.createdAt DESC")
    Page<ListBookingCarReportDTO> getListAccountReport (@Param("bookingStatus") BookingStatus bookingStatus, String driverId ,
                                                        @Param("bookingType")CarBookingType bookingType , LocalDateTime fromTime, LocalDateTime toTime, List<String> accountId, Pageable pageable);

    @Query(value = "SELECT c" +
            " FROM BookingCar c inner join BookingCarDetail cd ON c.id = cd.bookingCarId" +
            " where (:bookingStatus IS NULL OR c.bookingStatus = :bookingStatus)" +
            " AND (:driverId IS NULL OR cd.driverId = :driverId)" +
            " AND (:bookingType IS NULL OR c.typeBooking = :bookingType) " +
            " AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt, '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            " AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            " AND ((:accountId) IS NULL OR c.accountId IN (:accountId))" +
            " GROUP BY c.id, c.createdAt")
    Page<BookingCar> testX(@Param("bookingStatus") BookingStatus bookingStatus, String driverId ,
                           @Param("bookingType")CarBookingType bookingType , LocalDateTime fromTime, LocalDateTime toTime, List<String> accountId, Pageable pageable);


    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingcar.dto.GetListReqBookingCarResDTO(" +
            " c.id," +
            " c.typeBooking," +
            " c.original, " +
            " c.destination," +
            " c.timeStart, " +
            " c.timeReturn, " +
            " c.bookingStatus, " +
            " c.createdAt, " +
            " c.accountId, " +
            " c.approveLevel, " +
            " c.quantity, " +
            " c.distance " +
            " )" +
            " FROM BookingCar c " +
            "WHERE (:bookingStatus IS NULL OR c.bookingStatus = :bookingStatus)" +
            "AND (:unitId IS NULL OR c.unitId = :unitId)" +
            "AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            " " +
            "")
    Page<GetListReqBookingCarResDTO> getListBookingCarReq(
            @Param("bookingStatus") BookingStatus bookingStatus,
            LocalDateTime fromTime, LocalDateTime toTime,
            String unitId,
            Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingcar.dto.GetListReqBookingCarResDTO(" +
            "c.id," +
            " c.typeBooking," +
            " c.original, " +
            " c.destination," +
            " c.timeStart, " +
            " c.timeReturn, " +
            " c.bookingStatus, " +
            " c.createdAt, " +
            " c.accountId, " +
            " c.approveLevel, " +
            " c.quantity, " +
            " c.distance " +
            " )" +
            " FROM BookingCar c INNER JOIN  BookingCarDetail cd ON c.id = cd.bookingCarId " +
            "WHERE (:bookingStatus IS NULL OR c.bookingStatus = :bookingStatus)" +
            "AND (cd.driverId = :driverId)" +
            "AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d'))) " +
            "GROUP BY c.id")
    Page<GetListReqBookingCarResDTO> getListAssignedForDriver(
            @Param("bookingStatus") BookingStatus bookingStatus,
            LocalDateTime fromTime, LocalDateTime toTime, String driverId,
            Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingcar.dto.BookingCarResDTO(" +
            " cd.id," +
            " c.id," +
            " cd.type," +
            " c.typeBooking, " +
            " cd.original," +
            " cd.destination," +
            " c.createdAt, " +
            " cd.timeStart, " +
            " c.note, " +
            " c.bookingStatus," +
            " c.quantity," +
            " c.approveLevel," +
            " c.accountId," +
            " cd.driverId, " +
            " cd.carId," +
            " c.reason," +
            " cd.status," +
            " c.fuelEstimate," +
            " c.distance" +
            " )" +
            " FROM BookingCar c INNER JOIN  BookingCarDetail cd ON c.id = cd.bookingCarId " +
            "WHERE c.id = :id" +
            "")
    List<BookingCarResDTO> getDetail(@Param("id") String id);

    @Query(value = "SELECT c FROM BookingCar c " +
            "WHERE c.accountId = :id " +
            "order by c.createdAt DESC " +
            "")
    List<BookingCar> findLatestBookingCar(@Param("id") String id, Pageable pageable);

    @Query(value = "SELECT case when (count(b) > 0) then true else false end "+
            "FROM BookingCar b " +
            "WHERE b.accountId = :accountId AND b.bookingStatus <> 'CANCEL' " +
            "AND ((" +
            "   ( FUNCTION('SUBTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) < :timeStart " +
            "   AND :timeStart < FUNCTION('ADDTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) ) " +
            "   AND ( b.typeBooking = 'ONE_WAY' OR b.typeBooking = 'DRIVER_FOLLOW') " +
            ") " +
            "OR ( " +
            "   ( FUNCTION('SUBTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) < :timeStart AND " +
            "   :timeStart < FUNCTION('ADDTIME', b.timeReturn, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) ) " +
            "  AND b.typeBooking = 'TWO_WAY' " +
            " ) )")
    boolean checkExistsTimeWithOneWayOrFull(String accountId, LocalDateTime timeStart, Integer timeDelay);

    @Query(value = "SELECT case when (count(b) > 0) then true else false end " +
            "FROM BookingCar b " +
            "WHERE b.accountId = :accountId AND b.bookingStatus <> 'CANCEL' " +
            "AND ( " +
            "((( FUNCTION('SUBTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) <= :timeStart AND :timeStart <= FUNCTION('ADDTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)))" +
            "   OR ( FUNCTION('SUBTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) <= :timeReturn AND :timeReturn <= FUNCTION('ADDTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay *3600))) " +
            "   OR ( :timeStart <= b.timeStart AND b.timeStart <= :timeReturn) ) " +
            "   AND ( b.typeBooking = 'ONE_WAY' OR b.typeBooking = 'DRIVER_FOLLOW')" +
            ") " +
            "OR (" +
            "   (( :timeStart <= b.timeStart AND b.timeStart <= :timeReturn ) " +
            "   OR (:timeStart <= b.timeReturn AND b.timeReturn <= :timeReturn)) " +
            "   OR ( :timeStart < FUNCTION('SUBTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) AND FUNCTION('SUBTIME',b.timeStart, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) < :timeReturn ) " +
            "   OR ( :timeStart < FUNCTION('ADDTIME',b.timeReturn, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) AND FUNCTION('ADDTIME',b.timeReturn, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) < :timeReturn ) " +
            "   AND b.typeBooking = 'TWO_WAY' )" +
            ")")
    boolean checkExistsTimeWithTwoWay(String accountId, LocalDateTime timeStart, LocalDateTime timeReturn, Integer timeDelay);

    Optional<BookingCar> findByIdAndAccountId(String id, String accountId);
    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingcar.dto.ListBookingCarReportExportDTO(" +
            "c.id , c.typeBooking , c.original , c.destination , c.quantity , c.reason , c.accountId , c.bookingStatus, c.createdAt)" +
            " FROM BookingCar c inner join BookingCarDetail cd ON c.id = cd.bookingCarId" +
            " where (:bookingStatus IS NULL OR c.bookingStatus = :bookingStatus)" +
            " AND (:driverId IS NULL OR cd.driverId = :driverId)" +
            " AND (:bookingType IS NULL OR c.typeBooking = :bookingType) " +
            " AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            " AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', c.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            " AND (coalesce(:accountId) IS NULL OR c.accountId IN (:accountId))" +
            " group by c.id" +
            " ORDER BY c.createdAt DESC")
    List<ListBookingCarReportExportDTO> getListBookingReportReportExport (@Param("bookingStatus") BookingStatus bookingStatus, String driverId ,
                                                                          @Param("bookingType")CarBookingType bookingType , LocalDateTime fromTime, LocalDateTime toTime, List<String> accountId);


    @Query(value = "SELECT c " +
            " FROM BookingCar c JOIN BookingCarDetail detail ON c.id = detail.bookingCarId " +
            "WHERE (c.bookingStatus != 'DONE' OR c.bookingStatus != 'CANCEL')" +
            "AND (c.accountId = :accountId)" +
            "AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', detail.timeStart , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            " " +
            "")
    List<BookingCar> getListBookingCar(LocalDateTime fromTime, String accountId);

    @Query(value = "SELECT c " +
            " FROM BookingCar c JOIN BookingCarDetail detail ON c.id = detail.bookingCarId " +
            "WHERE (c.bookingStatus != 'DONE' OR c.bookingStatus != 'CANCEL')" +
            "AND (detail.driverId = :driverId)" +
            "AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', detail.timeStart , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            " " +
            "")
    List<BookingCar> getListBookingCarOfDriver(LocalDateTime fromTime, String driverId);

}
