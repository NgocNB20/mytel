package mm.com.mytelpay.family.repo;

import mm.com.mytelpay.family.model.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    Device findFirstByDeviceId(String deviceId);
    Optional<Device> findFirstByAccountId(String deviceId);

    @Modifying
    void deleteAllByAccountIdAndDeviceId(String accountId, String deviceId);
}
