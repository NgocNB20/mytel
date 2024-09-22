package mm.com.mytelpay.family.controller;

import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterReqDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.car.CarExcelService;
import mm.com.mytelpay.family.business.car.CarService;
import mm.com.mytelpay.family.business.car.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.validate.FileExcelRegex;
import mm.com.mytelpay.family.exception.validate.MultipartFileArrayRegex;
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
import java.io.IOException;
import mm.com.mytelpay.family.business.scanqrhistory.ScanQRHistoryService;
import mm.com.mytelpay.family.exception.ResourceErrorCode;
import mm.com.mytelpay.family.model.CarScanQRSHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Tag(name = "Car", description = "APIs for Services")
@RequestMapping("car/")
@Validated
@RestController
public class CarController extends BaseController {

    @Autowired
    private CarService carService;

    @Autowired
    private CarExcelService carExcelService;
    
    @Autowired
    private ScanQRHistoryService scanQRHistoryService;

    public Logger logger = LogManager.getLogger(CarController.class);

    @Operation(summary = "Get list car",
            description = "Return list car")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = CarFilterResDTO.class)))),
        @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getList", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getList(
            @RequestBody @Valid CarFilterReqDTO request,
            HttpServletRequest httpServletRequest
    ) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), carService.getList(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get detail of car",
            description = "Return detail of car")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CarDetailResDTO.class))),
        @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getDetail(
            @RequestBody @Valid SimpleRequest request,
            HttpServletRequest httpServletRequest
    ) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), carService.getDetail(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Create Car",
            description = "Returns car info")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "add", consumes = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> create(
            @RequestPart(value = "createCar") @Valid CarCreateReqDTO request,
            @RequestPart(value = "file", required = false) @Valid @MultipartFileArrayRegex MultipartFile[] file,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        if (carService.create(request, file, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Edit Car",
            description = "Returns car info")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "update", consumes = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> edit(
            @RequestPart(value = "editCar") @Valid CarEditReqDTO request,
            @RequestPart(value = "file", required = false) @Valid @MultipartFileArrayRegex MultipartFile[] files,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        if (carService.edit(request, files, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Delete Car",
            description = "Returns car info")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "delete",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> delete(
            @RequestBody @Valid SimpleRequest request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        if (carService.delete(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @PostMapping(value = "import",
             consumes = {MediaType.APPLICATION_JSON_VALUE,
                MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {"application/json"}
    )
    public ResponseEntity<ByteArrayResource> importExcel(
            @RequestPart(value = "file") @Valid @FileExcelRegex MultipartFile file,
            HttpServletRequest httpServletRequest
    ) throws IOException {
        if (ExcelCommon.hasExcelFormat(file)) {
            return carExcelService.importExcel(file, httpServletRequest);
        } else {
            throw new BusinessEx(null, CommonException.Request.INPUT_INVALID, "parameter.invalid.excelNotMatch");
        }
    }

    @PostMapping(value = "export",
            produces = {"application/json"}
    )
    public ResponseEntity<ByteArrayResource> exportExcel(
            @RequestBody @Valid CarExportReqDTO carExportReqDTO,
            HttpServletRequest httpServletRequest
    ) throws IOException {
        return carExcelService.exportExcel(carExportReqDTO, httpServletRequest);
    }

    @PostMapping(value = "template",
            produces = {"application/json"}
    )
    public ResponseEntity<ByteArrayResource> exportExampleExcel(
            HttpServletRequest httpServletRequest
    ) throws IOException {
        return carExcelService.downloadTemplate(httpServletRequest);
    }

    @Operation(summary = "Filter driver for booking", description = "Filter driver for booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
        @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getListCarForAssign", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getListDriverForBooking(@RequestBody @Valid CarAssignReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), carService.getListCarForAssign(request, httpServletRequest), (Object) null);

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping(value = "/carReport")
    public ResponseEntity<CommonResponseDTO> getListCarReport(@RequestBody CarReportReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), carService.getListCarReport(request, httpServletRequest), (Object) null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Valid QR Data", description = "Valid Data From Scan QR Code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The QR code is valid",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
        @ApiResponse(responseCode = "400", description = "The QR Code Is inValid", content = @Content)})
    @PostMapping(value = "validQR", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> validQRCodeData(@RequestBody @Valid CarQRReqDTO request, HttpServletRequest httpServletRequest) {
        
            CarQRDataResDTO carInfo = carService.validQRData(request);
            
            scanQRHistoryService.saveQRScanHistory(request, carInfo);
                    
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null, (Object) null);

            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        
    }
}
