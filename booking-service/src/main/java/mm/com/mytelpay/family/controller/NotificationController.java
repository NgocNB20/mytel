package mm.com.mytelpay.family.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.notification.NoticeService;
import mm.com.mytelpay.family.business.notification.dto.NotificationDeleteReqDTO;
import mm.com.mytelpay.family.business.notification.dto.NotificationFilterReqDTO;
import mm.com.mytelpay.family.business.notification.dto.SendNoticeReqDTO;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "notification", description = "APIs for Services")
@RequestMapping("")
@Validated
@RestController
public class NotificationController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    @Operation(summary = "Get List notification",
            description = "Return list of notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/user/family/listNotification", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> filter(
            @RequestBody @Valid NotificationFilterReqDTO reqDTO,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(reqDTO.getRequestId(), noticeService.filter(reqDTO, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Read notification",
            description = "Return detail of notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/user/family/readNotification", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> read(
            @RequestBody @Valid SimpleRequest request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), noticeService.readNotice(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Delete notification",
            description = "Delete notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/user/family/notification/delete", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> delete(
            @RequestBody @Valid NotificationDeleteReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        if (noticeService.delete(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Send notification",
            description = "Send notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/public/notification/sendNotice", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> sendFCM(
            @RequestBody @Valid SendNoticeReqDTO notice,
            HttpServletRequest httpServletRequest
    ) {
        if (noticeService.sendNotice(notice, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(notice.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(notice.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }
}
