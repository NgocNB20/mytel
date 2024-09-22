package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.model.entities.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {
    @Modifying
    void deleteByAccountId(String accountId);

    @Modifying
    @Query("DELETE FROM AccountRole as acr WHERE acr.accountId = :accountId AND acr.roleId <> :endUserId")
    void deleteByAccountIdExceptEndUserRole(String accountId, String endUserId);

    Optional<AccountRole> findByRoleId(String id);
    List<AccountRole> findByAccountId(String accountId);

    AccountRole findByAccountIdAndRoleId(String accountId, String roleId);
}
