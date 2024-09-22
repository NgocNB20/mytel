package mm.com.mytelpay.family.controller.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.account.dto.*;
import mm.com.mytelpay.family.business.account.dto.detail.AccountDetailResDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceReqDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountDeviceResDTO;
import mm.com.mytelpay.family.business.account.dto.device.AccountsInfoRequest;
import mm.com.mytelpay.family.business.account.dto.login.LoginReqDTO;
import mm.com.mytelpay.family.business.account.dto.RefreshTokenReqDTO;
import mm.com.mytelpay.family.business.account.service.AccountManageService;
import mm.com.mytelpay.family.business.account.service.AccountService;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.utils.Constants;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "Account", description = "APIs for business related to account, no need to login")
@RequestMapping("/public/user/family/")
@RestController
@Validated
public class AccountPublicController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountManageService accountManageService;
    @Value("${app.secretKey}")
    String secretKeyStr;

    @Operation(summary = "Login",
            description = "login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "login", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> login(@Valid @RequestBody LoginReqDTO loginReqDTO, HttpServletRequest servletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(loginReqDTO.getRequestId(), accountService.login(loginReqDTO, servletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Refresh Token ",
            description = "Return new token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "refreshToken", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenReqDTO refreshTokenRequest, HttpServletRequest servletRequest) {
        return accountService.refreshToken(refreshTokenRequest);
    }

    @Operation(summary = "Register new account",
            description = "Register new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "preRegister")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> preRegisterAccount(@Valid @RequestBody UserRegisterReqDTO request) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountService.preRegisterAccount(request));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Resend OTP",
            description = "Resend OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "resendOTP")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> resendOtp(@Valid @RequestBody ResendOtpReqDTO request) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountService.resendOtp(request));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Verify otp to create account",
            description = "Verify otp to create account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "verifyRegister", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> verifyOtpCreateAccount(@Valid @RequestBody UserVerifyOtpReqDTO verifyOtpRequest) throws JsonProcessingException {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(verifyOtpRequest.getRequestId(), accountService.verifyOtpCreateAccount(verifyOtpRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping(value = "forgotPass")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> forgotPassword(@Valid @RequestBody ForgetPasswordRequest forgetPasswordRequest, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        ActionLogIDDTO actionLogIDDTO = accountService.forgotPassword(forgetPasswordRequest, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(forgetPasswordRequest.getRequestId(), actionLogIDDTO);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "verify otp to reset account",
            description = "verify otp to reset account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "password/forget/verifyOtp", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> verifyOtpResetPassword(@Valid @RequestBody UserVerifyOtpReqDTO verifyOtpRequest, HttpServletRequest httpServletRequest) {
        try {
            if(accountService.verifyOtpResetPassword(verifyOtpRequest)){
                CommonResponseDTO commonResponse = Util.generateDefaultResponse(verifyOtpRequest.getRequestId() , null);
                return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
            }
        } catch (JsonProcessingException e) {
            throw new BusinessEx(verifyOtpRequest.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
        return null;
    }

    @Operation(summary = "reset password after verified otp",
            description = "reset password after verified otp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "verifyForgotPass", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest httpServletRequest) {
        if (accountService.resetPassword(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Get detail device of account", description = "Return detail device of account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDeviceResDTO.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getDevice", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getInfoDeviceAccountByRole(@RequestBody @Valid AccountDeviceReqDTO request, HttpServletRequest httpServletRequest) {
        String appSecretKeyHeader = httpServletRequest.getHeader(Constants.APP_SECRET_KEY);
        if (!StringUtils.equals(appSecretKeyHeader, secretKeyStr)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        List<AccountDeviceResDTO> accounts = accountManageService.getInfoDeviceAccountByRole(request, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accounts);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get devices of list accounts", description = "Return detail device of account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDeviceResDTO.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getListAccountDevice", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getInfoDeviceAccounts(@RequestBody @Valid AccountsInfoRequest request, HttpServletRequest httpServletRequest) {
        String appSecretKeyHeader = httpServletRequest.getHeader(Constants.APP_SECRET_KEY);
        if (!StringUtils.equals(appSecretKeyHeader, secretKeyStr)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        List<AccountDeviceResDTO> devicesAccount = accountManageService.getInfoDevicesByAccountIds(request, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), devicesAccount);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get accountInfo of list accounts", description = "Return list account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDeviceResDTO.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getListAccounts", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getInfoAccounts(@RequestBody @Valid AccountsInfoRequest request, HttpServletRequest httpServletRequest) {
        String appSecretKeyHeader = httpServletRequest.getHeader(Constants.APP_SECRET_KEY);
        if (!StringUtils.equals(appSecretKeyHeader, secretKeyStr)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        List<AccountDetailResDTO> devicesAccount = accountManageService.getInfoAccountInfoByAccountIds(request, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), devicesAccount);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}
