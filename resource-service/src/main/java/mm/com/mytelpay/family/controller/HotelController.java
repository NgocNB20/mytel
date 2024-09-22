package mm.com.mytelpay.family.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.hotel.HotelExcelService;
import mm.com.mytelpay.family.business.hotel.HotelService;
import mm.com.mytelpay.family.business.hotel.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.validate.FileExcelRegex;
import mm.com.mytelpay.family.exception.validate.MultipartFileArrayRegex;
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

@Tag(name = "Hotel", description = "APIs for Services")
@RestController
@RequestMapping("/hotel")
@Validated
public class HotelController extends BaseController {
    @Autowired
    private HotelService hotelService;
    @Autowired
    private HotelExcelService hotelExcelService;

    @Operation(summary = "Create Hotel",
            description = "Returns Hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/add",
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> createHotel(
            @RequestPart(value = "createHotel") @Valid HotelCreateReqDTO request,
            @RequestPart(value = "file", required = false) @Valid @MultipartFileArrayRegex MultipartFile[] file,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (hotelService.createHotel(request, file, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Filter hotel",
            description = "Return list of hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "filterForApp", produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> getHotelsByFilterForApp(@RequestBody @Valid HotelFilterReqDTO request, HttpServletRequest httpServletRequest) {
        return hotelService.getListForApp(request, httpServletRequest);
    }

    @Operation(summary = "Filter hotel for app",
            description = "Return list of hotel for app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = HotelFilterResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "filter", produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> getHotelsByFilter(@RequestBody @Valid HotelFilterReqDTO request, HttpServletRequest httpServletRequest) {
        return hotelService.getListForCms(request, httpServletRequest);
    }

    @Operation(summary = "Get hotels by hotel ids",
            description = "Get hotels by hotel ids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = HotelFilterResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getByIds", produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> getHotelsByIds(@RequestBody @Valid GetHotelsByIdsReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), hotelService.getHotelsByIds(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get detail of hotel",
            description = "Return detail of hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HotelDetailResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getDetail(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), hotelService.getDetail(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Edit Hotel",
            description = "Returns true/false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/edit",
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> editHotel(
            @RequestPart(value = "editHotel") @Valid HotelEditReqDTO request,
            @RequestPart(value = "file", required = false) @Valid @MultipartFileArrayRegex MultipartFile[] file,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (hotelService.editHotel(request, file, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Delete Hotel",
            description = "Returns true/fasle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "delete",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> deleteHotel(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest){
        hotelService.deleteHotel(request, httpServletRequest);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping(value = "importExcel"
            , consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"}
    )
    public ResponseEntity<ByteArrayResource> importExcel(@RequestPart(value = "file") @FileExcelRegex MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        if(ExcelCommon.hasExcelFormat(file)){
            return hotelExcelService.importExcel(file, httpServletRequest);
        }else{
            throw new BusinessEx(null, CommonException.Request.INPUT_INVALID, "parameter.invalid.excelNotMatch");
        }
    }

    @PostMapping(value = "exportExcel",
            produces = {"application/json"}
    )
    public ResponseEntity<ByteArrayResource> exportExcel(@RequestBody @Valid HotelExportReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        return hotelExcelService.exportExcel(request, httpServletRequest);
    }

    @Operation(summary = "Export template list hotel",
            description = "Return file excel of template list hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/exportTemplate", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> exportHotelTemplate(HttpServletRequest httpServletRequest) throws IOException {
        return hotelExcelService.exportTemplate(httpServletRequest);
    }

}
