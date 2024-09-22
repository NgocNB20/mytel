package mm.com.mytelpay.family.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.account.service.AccountExcelService;
import mm.com.mytelpay.family.business.account.service.AccountManageService;
import mm.com.mytelpay.family.business.account.dto.*;
import mm.com.mytelpay.family.business.account.dto.detail.AccountDetailResDTO;
import mm.com.mytelpay.family.business.account.dto.detail.AccountInfoPermissionResDTO;
import mm.com.mytelpay.family.business.account.dto.filter.AccountFilterReqDTO;
import mm.com.mytelpay.family.business.account.dto.filter.DriverAssignReqDTO;
import mm.com.mytelpay.family.business.account.dto.login.LoginRoleResDTO;
import mm.com.mytelpay.family.business.account.dto.profile.ProfileDetailResDTO;
import mm.com.mytelpay.family.business.account.dto.profile.ProfileEditReqDTO;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.validate.MultipartFileRegex;
import mm.com.mytelpay.family.filter.PerRequestContextDto;
import mm.com.mytelpay.family.logging.RequestUtils;
import mm.com.mytelpay.family.models.dto.BaseRequest;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import mm.com.mytelpay.family.business.account.dto.profile.CarRegistrationReqDTO;
import mm.com.mytelpay.family.utils.ObjectMapperUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mm.com.mytelpay.family.business.account.service.AccountSettingService;

@Tag(name = "Account management", description = "APIs for account management")
@RestController
@RequestMapping("/user")

@Validated
public class AccountManageController {
    @Autowired
    private AccountExcelService accountExcelService;
    @Autowired
    private AccountManageService accountManageService;
    @Autowired
    private PerRequestContextDto contextDto;
    
    @Autowired
    private AccountSettingService accountSettingService;

    private static final Logger logger = LogManager.getLogger(ObjectMapperUtil.class);

    @Operation(summary = "Action of admin to approve/reject to create account", description = "Action of admin to approve/reject to create account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponseDTO.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/approval", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> approvalAccount(@Valid @RequestBody AdminVerifiedAccountReqDTO request, HttpServletRequest httpServletRequest) {
        accountManageService.approvalAccount(request, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get account info", description = "Get account info")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getAccountDetails(@Valid @RequestBody SimpleRequest request, HttpServletRequest httpServletRequest) {
        AccountDetailResDTO account = accountManageService.getDetails(request, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), account);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get permission for CMS", description = "Get permission for CMS")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/family/getPermission", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getAllFunctionsOfUser(BaseRequest baseRequest, HttpServletRequest httpServletRequest) {
        String accountId = contextDto.getCurrentAccountId();
        List<LoginRoleResDTO> listRole = accountManageService.getAllFunctionsOfUser(accountId, baseRequest, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(baseRequest.getRequestId(), listRole);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Update account info", description = "Update account info")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/update", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> update(@Valid @RequestBody UpdateAccountReqDTO request, HttpServletRequest httpServletRequest) {
        accountManageService.update(request, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Add account", description = "Add account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/add", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> addAccount(@Valid @RequestBody AddAccountReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountManageService.add(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Filter account", description = "Filter account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getList", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> filterAccount(@RequestBody @Valid AccountFilterReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountManageService.filter(request, httpServletRequest));

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get List driver", description = "Filter account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/listDriver", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getListDriver(@RequestBody @Valid AccountFilterReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountManageService.getListDriver(request, httpServletRequest));

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "Filter driver for booking", description = "Filter driver for booking")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getListDriverForAssign", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getListDriverForBooking(@RequestBody @Valid DriverAssignReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountManageService.getListDriverAssign(request, httpServletRequest));

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get profile of account", description = "Return profile of account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDetailResDTO.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/family/profile", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getDetailProfile(@RequestBody @Valid BaseRequest request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountManageService.getDetailProfile(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "Update profile", description = "Returns profile info")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponseDTO.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/family/profile/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> updateProfile(
            @RequestPart(value = "updateProfile", required = false) @Valid ProfileEditReqDTO request
            , @RequestPart(value = "avatar", required = false) @Valid @MultipartFileRegex MultipartFile file
            , HttpServletRequest httpServletRequest) {
        if (request == null) {
            request = new ProfileEditReqDTO();
            request.setRequestId(RequestUtils.generateRequestId());
        }
        if (accountManageService.updateProfile(request, file, httpServletRequest)) {
            CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Get permission of account", description = "Return permission of account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountInfoPermissionResDTO.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getInfoPerUser", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getInfoPerUser(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountManageService.getInfoPerUser(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "delete account", description = "Return ")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleRequest.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/delete", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> deleteAccount(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) {
        if (accountManageService.delete(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Export teamplate account", description = "Export teamplate account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @GetMapping(value = "/template", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> template() {
        return accountExcelService.downloadTemplate();
    }

    @Operation(summary = "Export user", description = "Export user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/export", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> export(@RequestBody AccountExportDTO accountExportDTO) {
        return accountExcelService.exportExcel(accountExportDTO);
    }

    @Operation(summary = "Import user", description = "Import user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/import", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> importUser(@RequestPart(value = "file") MultipartFile file) {
        if(ExcelCommon.hasExcelFormat(file)){
            return accountExcelService.importExcel(file);
        }else{
            throw new BusinessEx(null, CommonException.Request.INPUT_INVALID, "parameter.invalid.excelNotMatch");
        }
    }

    @Operation(summary = "get list account", description = "get list account")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getListUser", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getListUser(@RequestBody AccountIdsDTO accountIdDTO , HttpServletRequest httpServletRequest){
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(accountIdDTO.getRequestId() , accountManageService.accountReportBookingCars(accountIdDTO , httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "get accountReport", description = "get accountReport")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getAccountReport", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getAccountReport(@RequestBody AccountMsisdnDTO accountMsisdnDTO , HttpServletRequest httpServletRequest){
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(accountMsisdnDTO.getRequestId() , accountManageService.accountReports(accountMsisdnDTO , httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
    
        @Operation(summary = "Update Car Registrations", description = "Update Car Registrations")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponseDTO.class)))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/family/profile/car_registration", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> carRegistration(@RequestBody @Valid CarRegistrationReqDTO request
            , HttpServletRequest httpServletRequest) {        
        if (accountSettingService.carRegistration(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        }else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }
    
    @Operation(summary = "Get account Settings", description = "Get account Settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class))))
            , @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/setting", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getAccountSettings(@Valid @RequestBody SimpleRequest request, HttpServletRequest httpServletRequest) {
        AccountSettingDTO accountSetting = accountSettingService.getAccountSetting(request, httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountSetting);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
