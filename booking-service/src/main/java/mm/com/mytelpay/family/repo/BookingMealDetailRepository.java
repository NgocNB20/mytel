package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.bookingmeal.dto.BookingMealFilterResDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.BookingMealViewListDTO;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.business.bookingmeal.dto.FilterBookingMealDetailDTO;
import mm.com.mytelpay.family.model.BookingMealDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingMealDetailRepository extends JpaRepository<BookingMealDetail, String> {

    @Query(value = "SELECT b FROM BookingMealDetail b INNER JOIN BookingMeal bm ON b.bookingMealId = bm.id " +
            "WHERE b.mealDay in :days and bm.accountId = :accountId and b.status <> 'CANCEL'")
    List<BookingMealDetail> findAllByMealDayIn(List<LocalDate> days, String accountId);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingmeal.dto.FilterBookingMealDetailDTO(" +
            "bd.id, bd.bookingMealId, bd.mealId, bd.type, bd.mealDay, bd.status, b.canteenId, bd.createdAt) " +
            "FROM BookingMealDetail bd INNER JOIN BookingMeal b ON b.id = bd.bookingMealId WHERE " +
            "b.accountId = :accountId AND " +
            "bd.mealDay IN :days ORDER BY FUNCTION('FIELD', bd.type, 'BREAKFAST', 'LUNCH', 'DINNER') ")
    List<FilterBookingMealDetailDTO> getDetailByMealDayIn(List<LocalDate> days, String accountId);

    @Query(value = "SELECT b FROM BookingMealDetail b INNER JOIN BookingMeal bm ON b.bookingMealId = bm.id " +
            "WHERE bm.accountId = :accountId " +
            "AND (:fromTime IS NULL OR b.mealDay >= :fromTime) " +
            "AND (:toTime IS NULL OR b.mealDay <= :toTime) " +
            " GROUP BY b.mealDay ")
    Page<BookingMealDetail> filterBookingMeal(LocalDate fromTime, LocalDate toTime, String accountId, Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingmeal.dto.BookingMealFilterResDTO(bmd.type, CASE WHEN COUNT(bmd) > 0 THEN COUNT(bmd) ELSE 0 END)"+
            "FROM BookingMealDetail bmd INNER JOIN BookingMeal bm ON bmd.bookingMealId = bm.id " +
            "WHERE (:status IS NULL OR bmd.status = :status) " +
            " AND (:from IS NULL OR bmd.mealDay >= :from)" +
            " AND (:to IS NULL OR bmd.mealDay <= :to) " +
            " AND bm.canteenId = :canteenId" +
            " group by bmd.type " +
            " ORDER BY CASE bmd.type " +
            " WHEN 'BREAKFAST' THEN 0 " +
            " WHEN 'LUNCH' THEN 1 " +
            " WHEN 'DINNER' THEN 2 " +
            " ELSE 3 END")
    List<BookingMealFilterResDTO> getTotalBookingMeal(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("status") MealDetailStatus status, @Param("canteenId") String canteenId);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingmeal.dto.BookingMealViewListDTO(bmd.id, bmd.status, bm.createdAt, bm.accountId) " +
            "FROM BookingMealDetail bmd " +
            "INNER JOIN BookingMeal bm ON bmd.bookingMealId = bm.id " +
            "WHERE (:status IS NULL OR bmd.status = :status) " +
            "AND (:from IS NULL OR bmd.mealDay >= :from) " +
            "AND (:to IS NULL OR bmd.mealDay <= :to) " +
            "AND (COALESCE(:accountId) IS NULL OR bm.accountId IN :accountId) " +
            "AND (:type IS NULL OR bmd.type = :type) " +
            "AND bm.canteenId = :canteenId")
    Page<BookingMealViewListDTO> getOrderMealList(
            LocalDate from,
            LocalDate to,
            MealDetailStatus status,
            MealType type,
            List<String> accountId,
            String canteenId,
            Pageable pageable
    );

    @Query(value = "SELECT b FROM BookingMealDetail b WHERE " +
            " b.type = :mealType AND b.mealDay = :mealDay AND b.status = :status")
    List<BookingMealDetail> getBookingMealDetailByMealDayAndMealType(LocalDate mealDay, MealType mealType, MealDetailStatus status);

    @Query(value = "SELECT b FROM BookingMealDetail b WHERE " +
            " b.mealId is null ")
    List<BookingMealDetail> getListMealIdNull();

    @Query(value = "SELECT b FROM BookingMealDetail b WHERE " +
            " b.mealDay < :date AND b.status = :status ")
    List<BookingMealDetail> getListBKMealDetailExpired(LocalDate date, MealDetailStatus status);
}
