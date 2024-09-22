package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.bookingmeal.dto.ReportFeeOrderMealResDTO;
import mm.com.mytelpay.family.business.bookingmeal.dto.ReportOrderMealResDTO;
import mm.com.mytelpay.family.enums.MealDetailStatus;
import mm.com.mytelpay.family.enums.MealType;
import mm.com.mytelpay.family.model.BookingMeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingMealRepository extends JpaRepository<BookingMeal, String> {

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingmeal.dto.ReportOrderMealResDTO( " +
            " bmd.id, bm.accountId, bmd.type," +
            " bmd.createdAt, bmd.mealDay, bm.canteenId, bm.unitId, bmd.status) " +
            "FROM BookingMealDetail bmd INNER JOIN BookingMeal bm on bm.id = bmd.bookingMealId " +
            "WHERE (coalesce(:accountId) IS NULL OR bm.accountId IN (:accountId)) " +
            "and (:canteenId is null or :canteenId = '' or bm.canteenId = :canteenId) " +
            "and (:mealType is null or bmd.type = :mealType) " +
            "and (:unitId is null or :unitId = '' or bm.unitId = :unitId) " +
            "and (:status is null or bmd.status = :status) " +
            "and (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', bmd.mealDay , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', bmd.mealDay , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d'))) " +
            " ORDER BY bmd.createdAt desc")
    Page<ReportOrderMealResDTO> filter(List<String> accountId, MealDetailStatus status, String canteenId,
                                       MealType mealType, String unitId, LocalDate fromTime, LocalDate toTime, Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingmeal.dto.ReportOrderMealResDTO( " +
            " bmd.id, bm.accountId, bmd.type," +
            " bmd.createdAt, bmd.mealDay, bm.canteenId, bm.unitId, bmd.status) " +
            "FROM BookingMealDetail bmd INNER JOIN BookingMeal bm on bm.id = bmd.bookingMealId " +
            "WHERE (coalesce(:accountId) IS NULL OR bm.accountId IN (:accountId)) " +
            "and (:canteenId is null or :canteenId = '' or bm.canteenId = :canteenId) " +
            "and (:mealType is null or bmd.type = :mealType) " +
            "and (:unitId is null or :unitId = '' or bm.unitId = :unitId) " +
            "and (:status is null or bmd.status = :status) " +
            "and (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', bmd.mealDay , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', bmd.mealDay , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d'))) " +
            " ORDER BY bmd.createdAt desc")
    List<ReportOrderMealResDTO> exportReport(List<String> accountId, MealDetailStatus status, String canteenId,
                                       MealType mealType, String unitId, LocalDate fromTime, LocalDate toTime);
    @Query(value = "SELECT b.canteenId FROM BookingMeal b WHERE b.id in :ids ")
    List<String> getCanteenIdsByBookingMealIds(List<String> ids);

    @Query(value = "SELECT b FROM BookingMeal b WHERE b.id in :ids GROUP BY b.accountId")
    List<BookingMeal> getAccountIdByBookingMealIds(List<String> ids);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingmeal.dto.ReportFeeOrderMealResDTO( " +
            " bmd.id, bm.accountId," +
            " bm.canteenId, bm.unitId, bmd.fee, bmd.status) " +
            "FROM BookingMealDetail bmd INNER JOIN BookingMeal bm on bm.id = bmd.bookingMealId " +
            "WHERE bmd.status <> 'PENDING' and (coalesce(:accountId) IS NULL OR bm.accountId IN (:accountId)) " +
            "and (:canteenId is null or :canteenId = '' or bm.canteenId = :canteenId) " +
            "and (:mealType is null or bmd.type = :mealType) " +
            "and (:unitId is null or :unitId = '' or bm.unitId = :unitId) " +
            "and (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', bmd.mealDay , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', bmd.mealDay , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d'))) " +
            " ORDER BY bmd.createdAt desc")
    Page<ReportFeeOrderMealResDTO> reportFeeBookingMeal(List<String> accountId, String canteenId,
                                       MealType mealType, String unitId, LocalDate fromTime, LocalDate toTime, Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.bookingmeal.dto.ReportFeeOrderMealResDTO( " +
            " bmd.id, bm.accountId," +
            " bm.canteenId, bm.unitId, bmd.fee, bmd.status) " +
            "FROM BookingMealDetail bmd INNER JOIN BookingMeal bm on bm.id = bmd.bookingMealId " +
            "WHERE bmd.status <> 'PENDING' and (coalesce(:accountId) IS NULL OR bm.accountId IN (:accountId)) " +
            "and (:canteenId is null or :canteenId = '' or bm.canteenId = :canteenId) " +
            "and (:mealType is null or bmd.type = :mealType) " +
            "and (:unitId is null or :unitId = '' or bm.unitId = :unitId) " +
            "and (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', bmd.mealDay , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            "AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', bmd.mealDay , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d'))) " +
            " ORDER BY bmd.createdAt desc")
    List<ReportFeeOrderMealResDTO> reportFeeBookingMealExport(List<String> accountId, String canteenId,
                                                        MealType mealType, String unitId, LocalDate fromTime, LocalDate toTime);
}
