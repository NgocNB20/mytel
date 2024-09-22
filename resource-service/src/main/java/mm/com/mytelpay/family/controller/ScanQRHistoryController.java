/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import mm.com.mytelpay.family.business.scanqrhistory.ScanQRHistoryService;
import mm.com.mytelpay.family.business.scanqrhistory.dto.CarScanQRHistoryFilterReqDTO;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "QR Scan History", description = "APIs for QR Scan History Services")
@RequestMapping("qrscan_history/")
@Validated
@RestController
public class ScanQRHistoryController extends BaseController{
    
    @Autowired
    ScanQRHistoryService scanQRHistoryService;
    
    @Operation(summary = "Car Scan QR History", description = "Car Scan QR History")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The QR code is valid",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
        @ApiResponse(responseCode = "400", description = "The QR Code Is inValid", content = @Content)})
    @PostMapping(value = "car/list", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> qrScanHistory(@RequestBody CarScanQRHistoryFilterReqDTO request, HttpServletRequest httpServletRequest) {
            
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), scanQRHistoryService.getScanQRHistoryList(request, httpServletRequest), (Object) null);

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
    
    @Operation(summary = "Detail for History Scan QR ", description = "Detail for History Scan QR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The QR code is valid",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
        @ApiResponse(responseCode = "400", description = "The QR Code Is inValid", content = @Content)})
    @PostMapping(value = "car/detail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> qrScanHistoryDetail(@RequestBody @Valid SimpleRequest request, HttpServletRequest httpServletRequest) {
            
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), scanQRHistoryService.getQRScanHistoryDetail(request, httpServletRequest), (Object) null);

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
