package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.model.entities.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface AccountInfoRepository extends JpaRepository<AccountInfo, String> {

    @Query(value = "SELECT acc FROM AccountInfo acc WHERE acc.accountId = :accountId")
    Optional<AccountInfo> findByAccountId(String accountId);

    AccountInfo findFirstByAccountId(String accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT acc FROM AccountInfo acc WHERE acc.accountId = :accountId")
    Optional<AccountInfo> findByAccountIdAndLock(String accountId);

}
