package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.model.entities.RoleHasFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleHasFunctionRepository extends JpaRepository<RoleHasFunction, Integer> {

    @Modifying
    @Query("DELETE FROM RoleHasFunction rhf WHERE rhf.roleId = :roleId")
    void deleteByRoleId(String roleId);

    @Modifying
    @Query("DELETE FROM RoleHasFunction rhf WHERE rhf.functionId = :functionId")
    void deleteByFunctionId(String functionId);

    Optional<RoleHasFunction> findByRoleIdAndAndFunctionId(String roleId , String functionId);

    List<RoleHasFunction> findByRoleId(String roleId );

    Optional<RoleHasFunction> findFirstByFunctionId(String functionId);
}
