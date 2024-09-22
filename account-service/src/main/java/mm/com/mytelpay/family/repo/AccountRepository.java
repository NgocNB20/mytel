package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.business.account.dto.AccountReportBookingCarDTO;
import mm.com.mytelpay.family.business.account.dto.detail.AccountDetailResDTO;
import mm.com.mytelpay.family.business.account.dto.detail.AccountInfoPermissionResDTO;
import mm.com.mytelpay.family.business.account.dto.filter.AccountFilterResDTO;
import mm.com.mytelpay.family.business.account.dto.filter.DriverFilterResDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO;
import mm.com.mytelpay.family.business.account.dto.profile.ProfileDetailResDTO;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.model.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Account findFirstByMsisdn(String msisdn);

    @Query(value = "SELECT acc FROM Account acc WHERE acc.id = :accountId ")
    Optional<Account> findByAccountId(String accountId);

    Account findFirstByMsisdnAndStatus(String msisdn, Status status);

    Account findFirstByEmail(String email);

    Account findFirstByEmailOrMsisdn(String email, String msisdn);

    @Query(value = "SELECT acc.id FROM Account acc WHERE acc.msisdn = :msisdn " +
            "")
    String findIdByMsisdn(String msisdn);

    Optional<Account> findByMsisdn(String msisdn);

    Optional<Account> findByEmail(String email);

    @Query(value = "SELECT acc FROM Account acc WHERE acc.id = :accountId AND (:status is null or acc.status = :status)")
    Optional<Account> findAccountIdAndStatus(String accountId, Status status);

    @Query(value = "SELECT ac FROM Account ac JOIN AccountInfo ai on ac.id = ai.accountId WHERE ac.id = :id AND ac.status = :status")
    Optional<Account> getAccountDetailsById(String id, Status status);

    @Query(value = "SELECT ac FROM Account ac JOIN AccountRole ar on ac.id = ar.accountId JOIN Role r on r.id = ar.roleId WHERE r.code = :roleCode")
    List<Account> getAccountByRoleCode(RoleType roleCode);

    @Query(value = "SELECT DISTINCT a.* FROM account a JOIN account_info ai ON a.id = ai.account_id" +
            "            LEFT JOIN account_role ar ON a.id = ar.account_id" +
            "            LEFT JOIN role r ON ar.role_id = r.id" +
            "            WHERE (:name IS NULL OR LOWER(ai.full_name) LIKE LOWER(CONCAT('%',:name,'%')))" +
            "            AND (:email IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%',:email,'%')))" +
            "            AND (:msisdn IS NULL OR LOWER(a.msisdn) LIKE LOWER(CONCAT('%',:msisdn,'%')))" +
            "            AND (:status IS NULL OR a.status = :status)" +
            "            AND (:roleCode IS NULL OR r.code = :roleCode))" +
            "            ORDER BY a.createdAt DESC", nativeQuery = true)
    Page<Account> filter(String name, String email, String msisdn, String status, String roleCode, Pageable pageable);


    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.profile.ProfileDetailResDTO ( " +
            "acc.id ," +
            "acc.msisdn ," +
            "accInfor.fullName ," +
            "acc.email ," +
            " u.name ," +
            " u.id ," +
            " acc.status, " +
            " acc.canteenId," +
            " accInfor.driverLicense, " +
            " accInfor.currentBalance " +
            ") " +
            "FROM Account acc INNER JOIN AccountInfo accInfor ON acc.id = accInfor.accountId " +
            "INNER JOIN Unit u ON acc.unitId = u.id " +
            "WHERE acc.id = :id and acc.status = 'ACTIVE'" +
            "")
    Optional<ProfileDetailResDTO> getDetailProfile(@Param("id") String id);

    @Query(value = "SELECT a FROM Account a WHERE " +
            " a.msisdn = :msisdn" +
            " AND a.status = :status ")
    Optional<Account> findByMsisdn(String msisdn, Status status);


    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO" +
            "(d.deviceId, " +
            " CASE WHEN d.accountId IS NULL THEN acc.id ELSE d.accountId END, " +
            " d.lang) " +
            " FROM Account acc" +
            " LEFT JOIN AccountRole ar ON acc.id = ar.accountId" +
            " LEFT JOIN Role r ON ar.roleId = r.id " +
            " LEFT JOIN Device d ON acc.id = d.accountId  " +
            " WHERE " +
            " (:unitId IS NULL OR acc.unitId = :unitId)" +
            " AND r.id = :roleId AND (:accountId IS NULL OR acc.id <> :accountId)")
    List<AccountDeviceResDTO> getInfoDeviceAccountByRole(String accountId, String unitId, String roleId);


    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO" +
            "(d.deviceId," +
            " CASE WHEN d.accountId IS NULL THEN acc.id ELSE d.accountId END, " +
            " d.lang) " +
            " FROM Account acc" +
            " LEFT JOIN Device d ON acc.id = d.accountId " +
            " WHERE " +
            " acc.id = :accId" +
            "  ")
    List<AccountDeviceResDTO> getInfoDeviceAccount(String accId);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.filter.AccountFilterResDTO" +
            "(acc.id, " +
            " accf.fullName, " +
            " acc.msisdn, " +
            " acc.email, " +
            " acc.unitId, " +
            " u.code, " +
            " u.name, " +
            " acc.status ) " +
            " FROM Account acc" +
            " JOIN AccountInfo accf ON acc.id = accf.accountId" +
            " JOIN Unit u ON acc.unitId = u.id " +
            " WHERE " +
            " (:name IS NULL OR LOWER(accf.fullName) LIKE LOWER(CONCAT('%',:name,'%'))) " +
            " AND (:email IS NULL OR LOWER(acc.email) LIKE LOWER(CONCAT('%',:email,'%')))" +
            " AND (:msisdn IS NULL OR LOWER(acc.msisdn) LIKE LOWER(CONCAT('%',:msisdn,'%')))" +
            " AND (COALESCE(:correctMsisdn, '') = '' OR acc.msisdn = :correctMsisdn) " +
            " AND (:unitId IS NULL OR u.id = :unitId)" +
            " AND (:status IS NULL OR acc.status = :status)" +
            "")
    Page<AccountFilterResDTO> getList(String name, String email, String msisdn, String correctMsisdn,
                                      String unitId, Status status, Pageable pageable);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.filter.DriverFilterResDTO" +
            "(acc.id, " +
            " accf.fullName, " +
            " acc.msisdn, " +
            " acc.email, " +
            " accf.driverLicense, " +
            " acc.status ) " +
            " FROM Account acc" +
            " JOIN AccountInfo accf ON acc.id = accf.accountId" +
            " JOIN AccountRole accRole ON acc.id = accRole.accountId" +
            " JOIN Role role ON accRole.roleId = role.id AND role.code='DRIVER'" +
            " WHERE " +
            " (:name IS NULL OR LOWER(accf.fullName) LIKE LOWER(CONCAT('%',:name,'%'))) " +
            " AND (:email IS NULL OR LOWER(acc.email) LIKE LOWER(CONCAT('%',:email,'%')))" +
            " AND (:msisdn IS NULL OR LOWER(acc.msisdn) LIKE LOWER(CONCAT('%',:msisdn,'%')))" +
            " AND (:status IS NULL OR acc.status = :status) ORDER BY acc.createdAt DESC " +
            "")
    List<DriverFilterResDTO> getListDriver(String name, String email, String msisdn, Status status);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.detail.AccountInfoPermissionResDTO ( " +
            "accInfor.fullName ," +
            "acc.msisdn ," +
            "u.id," +
            "u.code," +
            "u.name " +
            " ) " +
            "FROM Account acc INNER JOIN AccountInfo accInfor ON acc.id = accInfor.accountId " +
            "INNER JOIN Unit u ON acc.unitId = u.id " +
            "WHERE acc.id = :id " +
            "")
    Optional<AccountInfoPermissionResDTO> getDetailAccInfoPer(@Param("id") String id);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.filter.AccountFilterResDTO" +
            "(acc.id, " +
            " accf.fullName, " +
            " acc.msisdn, " +
            " acc.email, " +
            " acc.unitId, " +
            " u.code, " +
            " u.name, " +
            " acc.status ) " +
            " FROM Account acc" +
            " JOIN AccountInfo accf ON acc.id = accf.accountId" +
            " JOIN Unit u ON acc.unitId = u.id " +
            " WHERE " +
            " (:name IS NULL OR LOWER(accf.fullName) LIKE LOWER(CONCAT('%',:name,'%'))) " +
            " AND (:email IS NULL OR LOWER(acc.email) LIKE LOWER(CONCAT('%',:email,'%')))" +
            " AND (:msisdn IS NULL OR LOWER(acc.msisdn) LIKE LOWER(CONCAT('%',:msisdn,'%')))" +
            " AND (:unitCode IS NULL OR LOWER(u.code) LIKE LOWER(CONCAT('%',:unitCode,'%')))" +
            " AND (:status IS NULL OR acc.status = :status)" +
            " ORDER BY acc.createdAt DESC")
    List<AccountFilterResDTO> getListAccount(String name, String email, String msisdn,
                                             String unitCode, Status status);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.AccountReportBookingCarDTO(acc.id , accf.fullName , acc.msisdn)" +
            "   FROM Account acc" +
            "   JOIN AccountInfo accf ON acc.id = accf.accountId " +
            " WHERE (:id IS NULL OR LOWER(acc.id) LIKE LOWER(CONCAT('%',:id,'%')))")
    AccountReportBookingCarDTO listAccount(String id);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.AccountReportBookingCarDTO(acc.id , accf.fullName , acc.msisdn)" +
            "   FROM Account acc" +
            "   JOIN AccountInfo accf ON acc.id = accf.accountId " +
            " WHERE (:msisdn IS NULL OR LOWER(acc.msisdn) LIKE LOWER(CONCAT('%',:msisdn,'%')))")
    List<AccountReportBookingCarDTO> accountReports (String msisdn);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.filter.DriverFilterResDTO" +
            "(acc.id, " +
            " accf.fullName, " +
            " acc.msisdn, " +
            " acc.email, " +
            " accf.driverLicense, " +
            " acc.status ) " +
            " FROM Account acc" +
            " JOIN AccountInfo accf ON acc.id = accf.accountId" +
            " JOIN AccountRole accRole ON acc.id = accRole.accountId" +
            " JOIN Role role ON accRole.roleId = role.id AND role.code='DRIVER'" +
            " WHERE " +
            " (acc.status = 'ACTIVE') " +
            " AND (acc.id NOT IN :accountId) ORDER BY acc.createdAt DESC " +
            "")
    List<DriverFilterResDTO> getListDriverForAssign(List<String> accountId);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.detail.AccountDetailResDTO" +
            "(acc.id, " +
            " acc.email, " +
            " acc.msisdn, " +
            " accf.fullName, " +
            " acc.status ) " +
            " FROM Account acc" +
            " JOIN AccountInfo accf ON acc.id = accf.accountId" +
            " WHERE acc.id IN :accountIds" )
    List<AccountDetailResDTO> getListAccountsByIds(List<String> accountIds);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO" +
            "(d.deviceId," +
            " CASE WHEN d.accountId IS NULL THEN acc.id ELSE d.accountId END, " +
            " d.lang) " +
            " FROM Account acc" +
            " LEFT JOIN Device d ON acc.id = d.accountId " +
            " WHERE " +
            " acc.id in :accId" +
            "  ")
    List<AccountDeviceResDTO> getInfoDeviceByAccountIds(List<String> accId);
}
