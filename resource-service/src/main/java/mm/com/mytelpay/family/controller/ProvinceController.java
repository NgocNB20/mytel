package mm.com.mytelpay.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.model.Province;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.ExcelCommon;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.province.ProvinceServiceImpl;
import mm.com.mytelpay.family.business.province.dto.*;
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

@Tag(name ="Province" , description = "APIs for Services")
@RestController
@RequestMapping("/province")
@Validated
public class ProvinceController  extends BaseController {
    @Autowired
    ProvinceServiceImpl provinceService;
    @Operation(summary = "Create province",
            description = "Returns created province")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProvinceResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> create(@RequestBody @Valid ProvinceCreateReqDTO request, HttpServletRequest httpServletRequest) {
        Province province = provinceService.createProvince(request, httpServletRequest);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), province, (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Edit Province",
            description = "Returns province info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProvinceResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/edit",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> edit(@RequestBody @Valid ProvinceEditReqDTO request, HttpServletRequest httpServletRequest) {
        Province province = provinceService.editProvince(request, httpServletRequest);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), province, (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Delete Province",
            description = "Returns province info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/delete",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> delete(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) {
        provinceService.deleteProvince(request, httpServletRequest);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

    }

    @Operation(summary = "Get detail of province",
            description = "Return detail of province")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProvinceDetailResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getDetail(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest){
        ProvinceResDTO provinceResDTO = provinceService.getDetail(request, httpServletRequest);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId() , provinceResDTO , (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get list province",
            description = "Return list province")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProvinceFilerResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getList", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getList(@RequestBody ProvinceFilerResDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId() , provinceService.getList(request , httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "import excel",
            description = "Return list province")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProvinceResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/import"
            , consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"})
    public ResponseEntity<ByteArrayResource> importExcel(@RequestPart(value = "file") MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        if (ExcelCommon.hasExcelFormat(file)) {
            return provinceService.importExcel(file , httpServletRequest);
        } else {
            throw new BusinessEx(null, CommonException.Request.INPUT_INVALID, "parameter.invalid.excelNotMatch");
        }
    }

    @Operation(summary = "Export excel province",
            description = "Return file excel of province")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/export", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> exportProvince (@RequestBody @Valid ProvinceFilerResDTO request , HttpServletRequest httpServletRequest) throws IOException {
        return provinceService.exportExcel(request , httpServletRequest);
    }
    @Operation(summary = "Export template list province",
            description = "Return file excel of template list province")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/exportTemplate", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> exportProvinceTemplate(HttpServletRequest httpServletRequest) throws IOException {
        return provinceService.exportExcelTemplate(httpServletRequest);
    }

}
