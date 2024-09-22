package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.enums.AssignStatus;
import mm.com.mytelpay.family.enums.CarBookingDetailStatus;
import mm.com.mytelpay.family.model.BookingCarDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingCarDetailRepository extends JpaRepository<BookingCarDetail, String> {

    @Query(value = "SELECT cd from BookingCarDetail cd WHERE cd.id = :id AND cd.status = :assignStatus ")
    Optional<BookingCarDetail> findByIdAndAndAssignStatus(String id, AssignStatus assignStatus);

    @Query(value = "SELECT cd from BookingCarDetail cd WHERE cd.bookingCarId = :id")
    List<BookingCarDetail> findByBookingCarId(String id);

    @Query(value = "select case when (count(bc) > 0) then true else false end " +
            "from BookingCarDetail bc " +
            " where bc.bookingCarId = :id ")
    boolean checkBookingCarDetailExisted(String id);

    @Query(value = "SELECT cd from BookingCarDetail cd WHERE cd.id = :id AND cd.driverId = :driverId")
    Optional<BookingCarDetail> findByIdAndDriverId(String id, String driverId);
    @Query(value = "SELECT cd from BookingCarDetail cd WHERE cd.bookingCarId = :bookingCarId")
    BookingCarDetail bookingCarDetai(String bookingCarId);

    @Query(value = "SELECT detail FROM BookingCar c " +
            "JOIN BookingCarDetail detail ON c.id = detail.bookingCarId " +
            "WHERE ( c.bookingStatus = 'APPROVED' " +
            "OR detail.status = 'STARTED' OR (c.bookingStatus = 'APPROVING' AND c.approveLevel >= 2))" +
            "AND FUNCTION('DATE_FORMAT',detail.timeStart, '%Y-%m-%d') = FUNCTION('DATE_FORMAT',:timeStart, '%Y-%m-%d') " +
            "")
    List<BookingCarDetail> findBookingOutboundApprovedOrStarted(LocalDateTime timeStart);

    @Query(value = "select case when (count(b) > 0) then true else false end " +
            "FROM BookingCar b INNER JOIN BookingCarDetail bd ON b.id = bd.bookingCarId " +
            "WHERE bd.carId = :carId AND b.bookingStatus <> 'CANCEL' AND b.bookingStatus <> 'DONE' " +
            "AND bd.status <> 'CANCEL' AND bd.status <> 'DONE'")
    boolean checkCarOnTrip(String carId);

    @Modifying
    @Transactional
    @Query(value = "update BookingCarDetail bd set bd.status = :status where bd.bookingCarId = :bookingId ")
    void updateStatusBookingDetail(String bookingId, CarBookingDetailStatus status);

    @Query(value = "select b.accountId from BookingCar b " +
            " where b.id in :bookingId ")
    List<String> listAccountIdByBookingIds(List<String> bookingId);
}
