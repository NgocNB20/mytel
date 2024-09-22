package mm.com.mytelpay.family.repository;

import mm.com.mytelpay.family.business.applicationsetting.dto.AppSettingFilterResDTO;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.model.ApplicationSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationSettingRepository extends JpaRepository<ApplicationSetting, String> {

    @Query(value = "SELECT t FROM APPLICATION_SETTING t WHERE t.id = :id")
    Optional<ApplicationSetting> getAppSettingById(String id);

    @Query(value = "SELECT t FROM APPLICATION_SETTING t WHERE t.key = :key")
    Optional<ApplicationSetting> getAppSettingByName(String key);

    @Query(value = "SELECT t.value FROM APPLICATION_SETTING t WHERE t.key = :key")
    String getValueAppSettingByName(String key);

    @Query(value = "SELECT new mm.com.mytelpay.family.business.applicationsetting.dto.AppSettingFilterResDTO( " +
            "t.id, t.key, t.value, t.status)" +
            " FROM APPLICATION_SETTING t WHERE (:name IS NULL OR LOWER(t.key) LIKE LOWER(CONCAT('%', :name,'%'))) " +
            "AND (:status IS NULL OR t.status = :status) ")
    Page<AppSettingFilterResDTO> filterAppSetting(String name, Status status, Pageable pageable);

    @Query(value = "select case when (count(c) > 0) then true else false end " +
            "from APPLICATION_SETTING c " +
            "where c.key = :key")
    boolean existsKeyInApplicationSetting(String key);

    Optional<ApplicationSetting> findByKey(String key);
}
