/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.repo;

import java.util.Optional;
import mm.com.mytelpay.family.model.entities.AccountSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSettingRepository extends JpaRepository<AccountSetting, String>{
    Optional<AccountSetting> findOneByAccountId(String accountId);
}
