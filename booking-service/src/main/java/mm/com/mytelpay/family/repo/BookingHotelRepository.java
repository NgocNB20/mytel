package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.bookinghotel.dto.FilterBookingHotelResDTO;
import mm.com.mytelpay.family.business.bookinghotel.dto.ReportBookingHotelResDTO;
import mm.com.mytelpay.family.enums.BookingStatus;
import mm.com.mytelpay.family.model.BookingHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingHotelRepository extends JpaRepository<BookingHotel, String> {

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookinghotel.dto.FilterBookingHotelResDTO ( " +
            " bk.id, " +
            " bk.fromTime, " +
            " bk.toTime," +
            " bk.member , " +
            " bk.bookingStatus, " +
            " bk.createdAt, " +
            " bk.hotelId, " +
            " bk.accountId " +
            ") " +
            "FROM BookingHotel bk " +
            "WHERE (:status IS NULL OR bk.bookingStatus = :status) " +
            "AND (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', bk.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', bk.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            "And (:accountId IS NULL OR bk.accountId = :accountId)")
    Page<FilterBookingHotelResDTO> filter(BookingStatus status, LocalDate fromTime, LocalDate toTime, String accountId, Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookinghotel.dto.ReportBookingHotelResDTO(" +
            "bh.id, bh.accountId, bh.fromTime, bh.toTime, bh.createdAt, bh.member, bh.hotelId," +
            " bh.feeBooking, bh.feeService, bh.bookingStatus) " +
            "FROM BookingHotel bh " +
            "WHERE (coalesce(:accountId) IS NULL OR bh.accountId IN (:accountId)) " +
            "and (:hotelId is null or :hotelId = '' or bh.hotelId = :hotelId) " +
            "and (:status is null or bh.bookingStatus = :status) " +
            "and (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', bh.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', bh.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d'))) ORDER BY bh.createdAt desc")
    Page<ReportBookingHotelResDTO> getListBookingReport(List<String> accountId, BookingStatus status, String hotelId, LocalDate fromTime, LocalDate toTime, Pageable pageable);


    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookinghotel.dto.ReportBookingHotelResDTO(" +
            "bh.id, bh.accountId, bh.fromTime, bh.toTime, bh.createdAt, bh.member, bh.hotelId," +
            " bh.feeBooking, bh.feeService, bh.bookingStatus) " +
            "FROM BookingHotel bh " +
            "WHERE (coalesce(:accountId) IS NULL OR bh.accountId IN (:accountId)) " +
            "and (:hotelId is null or :hotelId = '' or bh.hotelId = :hotelId) " +
            "and (:status is null or bh.bookingStatus = :status) " +
            "and (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', bh.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', bh.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))  ORDER BY bh.createdAt desc")
    List<ReportBookingHotelResDTO> getListBookingReport(List<String> accountId, BookingStatus status, String hotelId, LocalDate fromTime, LocalDate toTime);

    @Query(value = "SELECT case when (count(b) > 0) then true else false end " +
            "FROM BookingHotel b " +
            "WHERE b.accountId = :accountId " +
            "AND (" +
            "   (( :from <= b.fromTime AND b.fromTime <= :to ) " +
            "   OR ( :from <= b.toTime AND b.toTime <= :to )) " +
            "   OR ( :from < FUNCTION('SUBTIME',b.fromTime, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) AND FUNCTION('SUBTIME',b.fromTime, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) < :to ) " +
            "   OR ( :from < FUNCTION('ADDTIME',b.toTime, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) AND FUNCTION('ADDTIME',b.toTime, FUNCTION('SEC_TO_TIME',:timeDelay * 3600)) < :to ) " +
            "  ) AND b.bookingStatus <> 'CANCEL'")
    boolean checkExistsTime(String accountId, LocalDateTime from, LocalDateTime to,  Integer timeDelay);

    @Query(value = "SELECT b FROM BookingHotel b WHERE b.hotelId = :hotelId ")
    List<BookingHotel> getListByHotelId(String hotelId);

    @Query(value = "SELECT b FROM BookingHotel b WHERE b.fromTime <= :from AND b.bookingStatus = 'PENDING' ")
    List<BookingHotel> getListBookingExpired(LocalDateTime from);
}
