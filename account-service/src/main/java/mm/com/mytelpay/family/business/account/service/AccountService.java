package mm.com.mytelpay.family.business.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mm.com.mytelpay.family.business.account.dto.*;
import mm.com.mytelpay.family.business.account.dto.login.LoginReqDTO;
import mm.com.mytelpay.family.business.account.dto.login.LoginResDTO;
import mm.com.mytelpay.family.business.account.dto.RefreshTokenReqDTO;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface AccountService {

    LoginResDTO login(LoginReqDTO request, HttpServletRequest servletRequest);

    ResponseEntity<CommonResponseDTO> refreshToken(RefreshTokenReqDTO request);

    String preRegisterAccount(UserRegisterReqDTO request);

    void validateMsisdnBeforeAddNew(final String msisdn, final String requestId);

    void validateEmailBeforeAddNew(final String email, final String requestId);

    void validateMatchPassword(final String password, final String confirmPassword, final String requestId);

    void validateUnitIdBeforeAddNew(final String unitId, final String requestId);

    boolean resendOtp(ResendOtpReqDTO request);

    boolean verifyOtpCreateAccount(UserVerifyOtpReqDTO request) throws JsonProcessingException;

    boolean changePassword(ChangePasswordRequest request) throws JsonProcessingException;

    boolean logout(LogoutReqDTO logoutRequest);

    ActionLogIDDTO forgotPassword(ForgetPasswordRequest forgetPasswordRequest, HttpServletRequest httpServletRequest) throws JsonProcessingException;

    boolean verifyOtpResetPassword(UserVerifyOtpReqDTO request) throws JsonProcessingException;

    boolean resetPassword(ResetPasswordRequest request, HttpServletRequest httpServletRequest);

    boolean lockAccount(String accountId);

}
