package mm.com.mytelpay.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.district.DistrictExcelService;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.district.DistrictService;
import mm.com.mytelpay.family.business.district.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
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

@Tag(name = "District", description = "APIs for Services")
@RestController
@RequestMapping("district/")
@Validated
public class DistrictController extends BaseController {
    @Autowired
    DistrictService districtService;

    @Autowired
    DistrictExcelService districtExcelService;

    @Operation(summary = "Create District",
            description = "Returns district info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DistrictResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "add", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> create(@RequestBody @Valid DistrictReqDTO districtRequest, HttpServletRequest httpServletRequest) {
        if (districtService.create(districtRequest, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(districtRequest.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(districtRequest.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Edit District",
            description = "Returns district info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DistrictResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "update",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> edit(@RequestBody @Valid DistrictEditReqDTO request, HttpServletRequest httpServletRequest) {
        if (districtService.edit(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Delete Districe",
            description = "Returns districe info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "delete",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> delete(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) {
        if (districtService.delete(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Get detail of district",
            description = "Return district of car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DistrictDetailResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getDetail(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), districtService.getDetail(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get list district",
            description = "Return list district")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DistrictFilterResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "filter", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getList(@RequestBody DistrictFilterResDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), districtService.getList(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "import excel",
            description = "Return list province")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DistrictResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "import"
            , consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"})
    public ResponseEntity<ByteArrayResource> importExcel(@RequestPart(value = "file") MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        if (ExcelCommon.hasExcelFormat(file)) {
            return districtExcelService.importExcel(file, httpServletRequest);
        } else {
            throw new BusinessEx(null, CommonException.Request.INPUT_INVALID, "parameter.invalid.excelNotMatch");
        }
    }

    @PostMapping(value = "export")
    public ResponseEntity<ByteArrayResource> exportExcel(@RequestBody DistrictFilterResDTO request, HttpServletRequest httpServletRequest) throws IOException {
        return districtExcelService.exportExcel(request, httpServletRequest);
    }

    @PostMapping(value = "downloadTemplate")
    public ResponseEntity<ByteArrayResource> downloadTemplate(HttpServletRequest httpServletRequest) throws IOException {
        return districtExcelService.downloadTemplate(httpServletRequest);
    }
}
