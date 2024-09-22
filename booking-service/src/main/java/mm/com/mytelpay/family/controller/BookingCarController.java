package mm.com.mytelpay.family.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.bookingcar.BookingCarReportService;
import mm.com.mytelpay.family.business.bookingcar.BookingCarService;
import mm.com.mytelpay.family.business.bookingcar.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.validate.MultipartFilePDFRegex;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Tag(name = "Booking Car", description = "APIs for Booking Car business")
@RequestMapping("/bookingCar")
@Validated
@RestController
@EnableScheduling
public class BookingCarController extends BaseController {

    @Autowired
    private BookingCarService bookingCarService;

    @Autowired
    private BookingCarReportService bookingCarReportService;

    @Operation(summary = "Filter Booking car",
            description = "Filter booking car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FilterBookingCarResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/listBookingCar")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> filterBookingCar(
            @RequestBody @Valid FilterBookingCarReqDTO request,
            HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingCarService.getList(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get List Request Booking car",
            description = "Get List Request booking car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FilterBookingCarResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getListRequest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getListRequest(
            @RequestBody @Valid GetListReqBookingCarReqDTO request,
            HttpServletRequest httpServletRequest) {
        request.setForBooking(false);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingCarService.getListReq(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get List Request Booking car For EU",
            description = "Get List Request booking car For EU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FilterBookingCarResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getListRequestEU")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getListRequestEU(
            @RequestBody @Valid GetListReqBookingCarReqDTO request,
            HttpServletRequest httpServletRequest) {
        request.setForBooking(true);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingCarService.getListReq(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get list request booking car information for role Driver",
            description = "Get list request booking car information for role Driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FilterBookingCarResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getListAssigned")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getListAssignedForDriver(
            @RequestBody @Valid GetListReqBookingCarReqDTO request,
            HttpServletRequest httpServletRequest) {
        request.setForBooking(true);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingCarService.getListAssignedForDriver(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Booking car",
            description = "Create request booking car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/create", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> createBookingCar(
            @RequestPart(value = "create") @Valid BookingCarReqDTO bookingCarRequest,
            @RequestPart(value = "file", required = false) @MultipartFilePDFRegex @Valid MultipartFile file,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (bookingCarService.bookingCar(bookingCarRequest, file, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(bookingCarRequest.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(bookingCarRequest.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Get detail Booking car",
            description = "Get detail booking car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookingCarResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/detail")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getDetail(
            @Valid @RequestBody SimpleRequest request,
            HttpServletRequest httpServletRequest) {
        List<BookingCarResDTO> res = bookingCarService.getDetailBookingCar(request, httpServletRequest);
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), res);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "Approve Booking car",
            description = "Approve booking car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/approve")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> approve(
            @Valid @RequestBody ApproveBookingCarReqDTO request,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (bookingCarService.approve(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Reject Booking car",
            description = "Reject booking car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/reject")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> reject(
            @Valid @RequestBody RejectBookingCarReqDTO request,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (bookingCarService.reject(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Update Booking car",
            description = "Update booking car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> update(
            @Valid @RequestBody UpdateBookingCarReqDTO request,
            HttpServletRequest httpServletRequest) {
        if (bookingCarService.update(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Verify QR for role Driver",
            description = "Verify QR for role Driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/verifyQr")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> verifyQrBookingCar(
            @Valid @RequestBody VerifyQrBookingCarReqDTO request,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingCarService.verifyQrBookingCar(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Complete trip for role Driver",
            description = "Complete trip for role Driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/complete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> completeBookingCar(
            @Valid @RequestBody CompleteBookingReqDTO request,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingCarService.completeBookingCar(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Cancel Booking car",
            description = "Cancel booking car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> cancel(@Valid @RequestBody BookingCarCancel bookingCarCancel, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        if (bookingCarService.cancel(bookingCarCancel, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(bookingCarCancel.getRequestId(), null, (Object) null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(bookingCarCancel.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @PostMapping(value = "/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getListAccount(@Valid @RequestBody ListAccountReportReqDTO listAccountReportReqDTO, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(null, bookingCarReportService.getListAccountReport(listAccountReportReqDTO, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Data for Assign",
            description = "Data for Assign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/assign/listCar")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> dataCarAssign(
            @RequestBody AssignReqDTO request,
            HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(null, bookingCarService.dataAssignCar(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Data for Assign",
            description = "Data for Assign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/assign/listDriver")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> dataDriverAssign(
            @RequestBody AssignReqDTO request,
            HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(null, bookingCarService.dataAssignDriver(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping(value = "/exportListReport")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> exportBookingCar(@RequestBody ListAccountReportReqDTO request, HttpServletRequest httpServletRequest) throws IOException {
        return bookingCarReportService.exportExcel(request, httpServletRequest);
    }

    @PostMapping(value = "/checkCarOnTrip")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> checkCarOnTrip(@RequestBody SimpleRequest request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(null, bookingCarService.checkCarOnTrip(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Check Request",
            description = "Check Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/checkRequest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> checkRequest(
            @RequestBody @Valid SimpleRequest request,
            HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingCarService.checkListRequestOfDriver(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Reject all Request of user",
            description = "Reject all Request of user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/rejectRequestUser")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> rejectRequestUser(
            @RequestBody @Valid SimpleRequest request,
            HttpServletRequest httpServletRequest) {
        if (bookingCarService.rejectRequestUser(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Update fuel estimate Booking car",
        description = "Update fuel estimate booking car")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommonResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/update/fuel-estimate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> updateFuelEst(
        @Valid @RequestBody UpdateFuelEstBookingCarReqDTO request,
        HttpServletRequest httpServletRequest) {
      if (bookingCarService.updateFuelEst(request, httpServletRequest)) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
      } else {
        throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
      }
    }


    @Operation(summary = "Calculate the distance between 2 locations",
        description = "Calculate the distance between 2 locations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommonResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/calculator-distance")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> calculatorDistance(
        @RequestBody @Valid CalculatorDistanceReqDTO request,
        HttpServletRequest httpServletRequest) {
      CommonResponseDTO commonResponse = generateDefaultResponse(null, bookingCarService.calculatorDistance(request, httpServletRequest));
      return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
