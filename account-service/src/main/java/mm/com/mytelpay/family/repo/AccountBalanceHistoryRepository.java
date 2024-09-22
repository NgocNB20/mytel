package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.balance.dto.GetBalanceHistoriesResDTO;
import mm.com.mytelpay.family.enums.BalanceActionType;
import mm.com.mytelpay.family.model.entities.AccountBalanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountBalanceHistoryRepository extends JpaRepository<AccountBalanceHistory, Long> {
    @Query(value = "SELECT new mm.com.mytelpay.family.business.balance.dto.GetBalanceHistoriesResDTO(" +
            " creater.fullName," +
            " subjecter.fullName," +
            " abh.subjectPhone, " +
            " abh.amount," +
            " abh.createdAt," +
            " abh.actionType )" +
            " FROM AccountBalanceHistory abh JOIN AccountInfo subjecter on abh.subjectId = subjecter.accountId " +
            " JOIN AccountInfo creater ON abh.createdBy = creater.accountId " +
            " WHERE (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', abh.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            " AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', abh.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            " AND (:createdBy IS NULL OR abh.createdBy = :createdBy) " +
            " AND (coalesce(:actionTypeList) IS NULL OR abh.actionType IN :actionTypeList) " +
            "")
    Page<GetBalanceHistoriesResDTO> getTopupHistory(LocalDateTime fromTime, LocalDateTime toTime, String createdBy, List<BalanceActionType> actionTypeList, Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.balance.dto.GetBalanceHistoriesResDTO(" +
            " creater.fullName," +
            " subjecter.fullName," +
            " abh.subjectPhone, " +
            " abh.amount," +
            " abh.createdAt," +
            " abh.actionType )" +
            " FROM AccountBalanceHistory abh JOIN AccountInfo subjecter on abh.subjectId = subjecter.accountId " +
            " JOIN AccountInfo creater ON abh.createdBy = creater.accountId " +
            " WHERE (:fromTime IS NULL OR (FUNCTION('DATE_FORMAT', abh.createdAt , '%Y-%m-%d') >= FUNCTION('DATE_FORMAT',:fromTime, '%Y-%m-%d')))" +
            " AND (:toTime IS NULL OR (FUNCTION('DATE_FORMAT', abh.createdAt , '%Y-%m-%d') <= FUNCTION('DATE_FORMAT',:toTime, '%Y-%m-%d')))" +
            " AND (:subjectId IS NULL OR abh.subjectId = :subjectId) " +
            " AND (coalesce(:actionTypeList) IS NULL OR abh.actionType IN :actionTypeList) " +
            "")
    Page<GetBalanceHistoriesResDTO> getBalanceHistory(LocalDateTime fromTime, LocalDateTime toTime, String subjectId, List<BalanceActionType> actionTypeList, Pageable pageable);

}
