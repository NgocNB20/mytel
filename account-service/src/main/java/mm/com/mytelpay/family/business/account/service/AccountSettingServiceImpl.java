/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.business.account.service;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.account.dto.AccountSettingDTO;
import mm.com.mytelpay.family.business.account.dto.profile.CarRegistrationReqDTO;
import mm.com.mytelpay.family.enums.Status;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.model.entities.AccountSetting;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.repo.AccountRepository;
import mm.com.mytelpay.family.utils.ObjectMapperUtil;
import mm.com.mytelpay.family.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mm.com.mytelpay.family.repo.AccountSettingRepository;

@Service
public class AccountSettingServiceImpl extends AccountBaseBusiness implements AccountSettingService{
    
    @Autowired
    private PerRequestContextDto perRequestContextDto;
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AccountSettingRepository accountSettingRepository;
    
    @Override
    public boolean carRegistration(CarRegistrationReqDTO request, HttpServletRequest httpServletRequest) {
        String id = perRequestContextDto.getCurrentAccountId();
        Account account = accountRepository.findAccountIdAndStatus(id, Status.ACTIVE).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId(), AccountErrorCode.ACCOUNT.WRONG_INFO, null);
        });
        
        Optional<AccountSetting> rawConfig = accountSettingRepository.findOneByAccountId(account.getId());
        
        AccountSetting config;
        
        if(rawConfig.isPresent()){
            config = rawConfig.get();
            config.setCarRegistrationActive(request.getCarRegistrationActive());
            config.setCarRegistrationTime(Util.getCurrentLocalDateTime());
        } else {
            config = new AccountSetting();
            config.setAccountId(account.getId());
            config.setCarRegistrationActive(request.getCarRegistrationActive());
            config.setCarRegistrationTime(Util.getCurrentLocalDateTime());
        }
        accountSettingRepository.save(config);
        
        return true;
    }    

    @SneakyThrows
    @Override
    public AccountSettingDTO getAccountSetting(String accountId) {
        AccountSettingDTO config;
        
        Optional<AccountSetting> rawConfig = accountSettingRepository.findOneByAccountId(accountId);

        if(rawConfig.isEmpty()){
            return new AccountSettingDTO();
        }
        
        return ObjectMapperUtil.convert(rawConfig.get(), AccountSettingDTO.class);
    }

    @SneakyThrows
    @Override
    public AccountSettingDTO getAccountSetting(SimpleRequest request, HttpServletRequest httpServletRequest) {
        
        Optional<AccountSetting> rawSettings = accountSettingRepository.findOneByAccountId(request.getId());

        if(rawSettings.isEmpty()){
            return new AccountSettingDTO();
        }
        
        return ObjectMapperUtil.convert(rawSettings.get(), AccountSettingDTO.class);
    }
}
