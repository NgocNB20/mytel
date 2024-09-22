package mm.com.mytelpay.family.controller.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.business.account.service.AccountService;
import mm.com.mytelpay.family.business.account.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Account", description = "APIs for business related to account")
@RequestMapping("/user/family/")
@RestController
@Validated
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private PerRequestContextDto perRequestContextDto;

    @Operation(summary = "user change password",
            description = "user change password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChangePasswordRequest.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "changePass", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) throws JsonProcessingException {
        if (accountService.changePassword(changePasswordRequest)) {
            CommonResponseDTO commonResponse = Util.generateDefaultResponse(changePasswordRequest.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(changePasswordRequest.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Logout",
            description = "Logout API  ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "logout", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> logout(@Valid @RequestBody LogoutReqDTO logoutRequest, HttpServletRequest servletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(logoutRequest.getRequestId(), accountService.logout(logoutRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Lock account",
            description = "Lock account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "lockAccount", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> lockAccount(HttpServletRequest servletRequest) {
        String currentUserId = perRequestContextDto.getCurrentAccountId();
        return ResponseEntity.status(HttpStatus.OK).body(accountService.lockAccount(currentUserId));
    }

}
