package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.model.NotificationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, String> {

    @Query(value = "SELECT n FROM NotificationHistory n WHERE n.accountId = :accountId " +
            "AND (:isRead IS NULL OR n.isRead = :isRead)")
            Page<NotificationHistory> filterNotification(String accountId, Boolean isRead, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM NotificationHistory nh WHERE nh.accountId = :accountId")
    void deleteByAccountId(String accountId);

    Optional<NotificationHistory> findFirstByIdAndAccountId(String id, String accountId);
}
