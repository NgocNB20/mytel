package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.account.dto.detail.PermissionAccountDTO;
import mm.com.mytelpay.family.business.role.dto.FilterRoleResDTO;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.model.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    @Query(value = "SELECT role.id FROM Role role WHERE role.code = :code")
    Optional<String> findRoleIdUsingRoleCode(RoleType code);

    @Query(value = "SELECT role" +
            " FROM Role role" +
            " JOIN AccountRole accountRole ON role.id = accountRole.roleId" +
            " WHERE accountRole.accountId = :accountId ")
    List<Role> findRoleByAccountId(String accountId);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.role.dto.FilterRoleResDTO(r.id , r.name, r.code)" +
            " FROM  Role  r WHERE ((:name IS NULL) OR (LOWER(r.name) LIKE LOWER(CONCAT('%', :name,'%')))) " +
            " AND (:code IS NULL OR r.code = :code)" +
            " ORDER BY r.createdAt DESC")
    Page<FilterRoleResDTO> getList(String name, RoleType code, Pageable pageable);

    Optional<Role> findRoleByCode (RoleType code);
    Optional<Role> findRoleById (String id);

    Role findByCode(RoleType code);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.detail.PermissionAccountDTO" +
            "(r.id , r.code, r.name )" +
            " FROM  Role  r LEFT JOIN AccountRole ar ON r.id = ar.roleId" +
            " WHERE " +
            " ar.accountId = :accountId" +
            "")
    List<PermissionAccountDTO> getListPerAcc(String accountId);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.detail.PermissionAccountDTO" +
            "(r.id, r.code , r.name )" +
            " FROM  Role  r " +
            "")
    List<PermissionAccountDTO> getListAllPer();

    @Query(value = "SELECT new mm.com.mytelpay.family.business.role.dto.FilterRoleResDTO(r.id , r.name , r.code) FROM Role r ORDER BY r.createdAt DESC")
    List<FilterRoleResDTO> getAllWithoutPagination();

    @Query(value = "SELECT r FROM Role r WHERE (:id IS NULL OR r.id = :id) AND (:type IS NULL OR r.code = :type)")
    Optional<Role> findFirstByIdOrCode(String id, RoleType type);

}
