package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.function.dto.FunctionFilterResDTO;
import mm.com.mytelpay.family.model.entities.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<Function, String> {

    @Query(value = "SELECT DISTINCT f.code FROM Function f JOIN RoleHasFunction rhf ON f.id = rhf.functionId WHERE rhf.roleId = :roleId")
    List<String> findFunctionCodesUsingRoleId(String roleId);

    @Query(value = "SELECT f FROM Function f JOIN RoleHasFunction rhf ON f.id = rhf.functionId WHERE rhf.roleId = :roleId")
    List<Function> findFunctionsUsingRoleId(String roleId);

    @Modifying
    @Query(value = "UPDATE Function f SET f.parentId = null WHERE f.parentId = :parentId")
    void updateParentIdWhenDeleteFunction(String parentId);

    Function findFirstByCode(String code);

    List<Function> findAllByParentId(String parentId);

    @Query(value = "SELECT f FROM Function f JOIN RoleHasFunction rhf ON f.id = rhf.functionId" +
            " JOIN AccountRole ar ON rhf.roleId = ar.roleId" +
            " WHERE ar.accountId = :accountId")
    List<Function> getActiveFunctionsByAccount(String accountId);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.function.dto.FunctionFilterResDTO(f.id, f.code, f.name, f.endPoints, f.parentId, p.name) " +
            "FROM Function f LEFT JOIN Function p ON f.parentId = p.id " +
            "WHERE (:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%',:name,'%'))) " +
            "AND (:code IS NULL OR LOWER(f.code) LIKE LOWER(CONCAT('%',:code,'%'))) " +
            "AND (COALESCE(:parentId, '') = '' OR f.parentId = :parentId) " +
            "ORDER BY f.createdAt DESC")
    Page<FunctionFilterResDTO> filterFunction(String name, String code, String parentId, Pageable pageable);


}
