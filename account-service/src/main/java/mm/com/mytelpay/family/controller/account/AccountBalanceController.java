package mm.com.mytelpay.family.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.account.dto.ChangePasswordRequest;
import mm.com.mytelpay.family.business.account.service.AccountBalanceService;
import mm.com.mytelpay.family.business.balance.dto.GetBalanceHistoriesReqDTO;
import mm.com.mytelpay.family.business.balance.dto.GetBalanceHistoriesResDTO;
import mm.com.mytelpay.family.business.balance.dto.TopupReqDTO;
import mm.com.mytelpay.family.business.balance.dto.UpdateBalanceReqDTO;
import mm.com.mytelpay.family.enums.BalanceActionType;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.utils.Constants;
import mm.com.mytelpay.family.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Account", description = "APIs for business related to account")
@RequestMapping("/balance")
@RestController
@Validated
public class AccountBalanceController {

    @Autowired
    private AccountBalanceService accountBalanceService;
    @Value("${app.secretKey}")
    String secretKeyStr;

    @Operation(summary = "Add or subtract user's balance",
            description = "Add or subtract user's balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChangePasswordRequest.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/user/update", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> updateBalance(@Valid @RequestBody UpdateBalanceReqDTO request, HttpServletRequest servletRequest) {
        String appSecretKeyHeader = servletRequest.getHeader(Constants.APP_SECRET_KEY);
        if (!StringUtils.equals(appSecretKeyHeader, secretKeyStr)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(null, accountBalanceService.updateBalance(request, servletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Chef add balance for user",
            description = "Chef add balance for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/chef/topup", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> topupBalance(@Valid @RequestBody TopupReqDTO request, HttpServletRequest servletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), accountBalanceService.topupBalance(request, servletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get topup history of Chef",
            description = "Get topup history of Chef")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/chef/history", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Page<GetBalanceHistoriesResDTO>> getTopupHistory(@Valid @RequestBody GetBalanceHistoriesReqDTO request, HttpServletRequest servletRequest) {
        List<BalanceActionType> actionTypeList = List.of(BalanceActionType.TOPUP);
        return ResponseEntity.status(HttpStatus.OK).body(accountBalanceService.getTopupHistories(request, actionTypeList, servletRequest));
    }

    @Operation(summary = "Get balance history of End User",
            description = "Get balance history of End User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/user/history", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Page<GetBalanceHistoriesResDTO>> getBalanceHistory(@Valid @RequestBody GetBalanceHistoriesReqDTO request, HttpServletRequest servletRequest) {
        List<BalanceActionType> actionTypeList = Arrays.asList(BalanceActionType.TOPUP, BalanceActionType.ORDER_MEAL, BalanceActionType.REFUND);
        return ResponseEntity.status(HttpStatus.OK).body(accountBalanceService.getBalanceHistories(request, actionTypeList, servletRequest));
    }

}
