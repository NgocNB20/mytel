package mm.com.mytelpay.family.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.canteen.CanteenExcelService;
import mm.com.mytelpay.family.business.canteen.CanteenService;
import mm.com.mytelpay.family.business.canteen.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
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
import java.io.IOException;

@Tag(name = "Canteen", description = "APIs for Services")
@RestController
@RequestMapping("canteen/")
@Validated
public class CanteenController  extends BaseController {
    @Autowired
    private CanteenService canteenService;
    @Autowired
    private CanteenExcelService canteenExcelService;

    @Operation(summary = "Get List canteens",
            description = "Return list of canteens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CanteenFilterResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "filter", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> filter(@RequestBody CanteenFilterReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), canteenService.filter(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get detail of canteen",
            description = "Return detail of canteen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CanteenDetailResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getDetail(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), canteenService.getDetail(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Create Canteen",
            description = "Returns canteen info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CanteenResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "add", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> create(@RequestBody @Valid CanteenCreateReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (canteenService.create(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Edit Canteen",
            description = "Returns canteen info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CanteenResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "edit", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> edit(@RequestBody @Valid CanteenEditReqDTO request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (canteenService.edit(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Delete Canteen",
            description = "Returns canteen info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "delete",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> delete(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (canteenService.delete(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @PostMapping(value = "importExcel"
            , consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"})
    public ResponseEntity<ByteArrayResource> importExcel(@RequestPart(value = "file") MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        if (ExcelCommon.hasExcelFormat(file)) {
            return canteenExcelService.importExcel(file, httpServletRequest);
        } else {
            throw new BusinessEx(null, CommonException.Request.INPUT_INVALID, "parameter.invalid.excelNotMatch");
        }
    }

    @PostMapping(value = "exportExcel",
            produces = {"application/json"})
    public ResponseEntity<ByteArrayResource> exportExcel(@RequestBody CanteenFilterReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        return canteenExcelService.exportExcel(request, httpServletRequest);
    }

    @PostMapping(value = "downloadTemplate",  produces = {"application/json"})
    public ResponseEntity<ByteArrayResource> downloadTemplate(HttpServletRequest httpServletRequest) throws IOException {
        return canteenExcelService.downloadTemplate(httpServletRequest);
    }

    @PostMapping(value = "getByIds", produces = {"application/json"})
    public ResponseEntity<CommonResponseDTO> getListByIds(@RequestBody CanteenGetIdsResDTO canteenGetIdsResDTO, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(null, canteenService.getListByIds(canteenGetIdsResDTO, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping("/getCanteenForChef")
    public ResponseEntity<CommonResponseDTO> getCanteenForChef(@RequestBody CanteenForChefReqDTO reqDTO, HttpServletRequest httpServletRequest){
        CommonResponseDTO commonResponse = generateDefaultResponse(null, canteenService.getCanteenForChef(reqDTO, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping("checkCanteenForChef")
    public  ResponseEntity<CommonResponseDTO> checkCanteenForChef(@Valid @RequestBody CheckCanteenForChefReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponseDTO = generateDefaultResponse(null, canteenService.checkCanteenId(reqDTO, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponseDTO);
    }

    @PostMapping("getAllCanteen")
    public ResponseEntity<CommonResponseDTO> getAllCanteen(HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponseDTO = generateDefaultResponse(null, canteenService.getAllCanteen(httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponseDTO);
    }
}
