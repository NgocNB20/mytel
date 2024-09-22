package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.enums.ActionType;
import mm.com.mytelpay.family.model.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ActionLogRepository extends JpaRepository<ActionLog, String> {
    Optional<ActionLog> findById(String id);
    Optional<ActionLog> findByIdAndMsisdn(String id, String msissdn);

    ActionLog findFirstByAccountIdAndIsVerifiedOtpAndActionTypeOrderByLastUpdatedAtDesc(String accountId, Boolean isVerifiedOtp, ActionType actionType);

}
