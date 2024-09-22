/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.account.service;

import javax.servlet.http.HttpServletRequest;
import mm.com.mytelpay.family.business.account.dto.AccountSettingDTO;
import mm.com.mytelpay.family.business.account.dto.profile.CarRegistrationReqDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;

public interface AccountSettingService {
    
    public boolean carRegistration(CarRegistrationReqDTO request, HttpServletRequest httpServletRequest);
    
    public AccountSettingDTO getAccountSetting(String accountId);
    
    public AccountSettingDTO getAccountSetting(SimpleRequest request, HttpServletRequest httpServletRequest);
}
