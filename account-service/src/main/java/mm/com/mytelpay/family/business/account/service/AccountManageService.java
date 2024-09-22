package mm.com.mytelpay.family.business.account.service;

import mm.com.mytelpay.family.business.account.dto.*;
import mm.com.mytelpay.family.business.account.dto.detail.AccountDetailResDTO;
import mm.com.mytelpay.family.business.account.dto.detail.AccountInfoPermissionResDTO;
import mm.com.mytelpay.family.business.account.dto.filter.AccountFilterReqDTO;
import mm.com.mytelpay.family.business.account.dto.filter.AccountFilterResDTO;
import mm.com.mytelpay.family.business.account.dto.filter.DriverAssignReqDTO;
import mm.com.mytelpay.family.business.account.dto.filter.DriverFilterResDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceReqDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountsInfoRequest;
import mm.com.mytelpay.family.business.account.dto.login.LoginRoleResDTO;
import mm.com.mytelpay.family.business.account.dto.profile.ProfileDetailResDTO;
import mm.com.mytelpay.family.business.account.dto.profile.ProfileEditReqDTO;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import mm.com.mytelpay.family.business.account.dto.profile.CarRegistrationReqDTO;

public interface AccountManageService {

    AccountDetailResDTO getDetails(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean update(UpdateAccountReqDTO request, HttpServletRequest httpServletRequest);

    List<LoginRoleResDTO> getAllFunctionsOfUser(String accountId, BaseRequest baseRequest, HttpServletRequest httpServletRequest);

    boolean delete(SimpleRequest request, HttpServletRequest httpServletRequest);

    boolean add(AddAccountReqDTO request, HttpServletRequest httpServletRequest);

    boolean approvalAccount(AdminVerifiedAccountReqDTO request, HttpServletRequest httpServletRequest);

    ProfileDetailResDTO getDetailProfile(BaseRequest request, HttpServletRequest httpServletRequest);

    boolean updateProfile(ProfileEditReqDTO request, MultipartFile files, HttpServletRequest httpServletRequest);

    List<AccountDeviceResDTO> getInfoDeviceAccountByRole(AccountDeviceReqDTO request, HttpServletRequest httpServletRequest);

    Page<AccountFilterResDTO> filter(AccountFilterReqDTO request, HttpServletRequest httpServletRequest);

    List<DriverFilterResDTO> getListDriver(AccountFilterReqDTO request, HttpServletRequest httpServletRequest);

    List<DriverFilterResDTO> getListDriverAssign(DriverAssignReqDTO request, HttpServletRequest httpServletRequest);

    AccountInfoPermissionResDTO getInfoPerUser(SimpleRequest request, HttpServletRequest httpServletRequest);

    List<AccountReportBookingCarDTO> accountReportBookingCars (AccountIdsDTO accountIdDTO, HttpServletRequest httpServletRequest);

    List<AccountReportBookingCarDTO> accountReports (AccountMsisdnDTO accountMsisdnDTO, HttpServletRequest httpServletRequest);

    List<AccountDeviceResDTO> getInfoDevicesByAccountIds(AccountsInfoRequest request, HttpServletRequest httpServletRequest);

    List<AccountDetailResDTO> getInfoAccountInfoByAccountIds(AccountsInfoRequest request, HttpServletRequest httpServletRequest);
}
