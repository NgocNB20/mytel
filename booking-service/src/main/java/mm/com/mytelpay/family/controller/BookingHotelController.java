package mm.com.mytelpay.family.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.bookinghotel.BookingHotelReportService;
import mm.com.mytelpay.family.business.bookinghotel.BookingHotelService;
import mm.com.mytelpay.family.business.bookinghotel.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.validate.MultipartFileRegex;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Tag(name = "bookHotel", description = "APIs for Services")
@RequestMapping("/bookingHotel/")
@Validated
@RestController
public class BookingHotelController extends BaseController {

    @Autowired
    private BookingHotelService bookingHotelService;

    @Autowired
    private BookingHotelReportService bookingHotelReportService;

    @Operation(summary = "Get List booking hotels",
            description = "Return list of booking hotels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "filterForEU", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> filterForEu(
            @Valid @RequestBody FilterBookingHotelReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingHotelService.filterForEU(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get List booking hotels role Admin",
            description = "Return list of booking hotels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "filterForAdmin", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> filterForAdmin(
            @Valid @RequestBody FilterBookingHotelReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingHotelService.filterForAdmin(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Create Booking Hotel Request",
            description = "Returns booking info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "create",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> create(
            @RequestBody @Valid BookingHotelCreateReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        if (bookingHotelService.create(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Get detail Booking Hotel Request",
            description = "Returns booking info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "detail",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> getDetail(
            @RequestBody @Valid BookingHotelDetailReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingHotelService.getDetail(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Cancel Booking Hotel Request",
            description = "Cancel Booking Hotel Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "cancel",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> cancel(
            @RequestBody @Valid CancelBookingHotelReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingHotelService.cancel(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Approve or Reject Booking Hotel Request",
            description = "Approve or Reject Booking Hotel Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "update",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> approveReject(
            @RequestPart(value = "update") @Valid ApproveRejectBookingHotelReqDTO request,
            @RequestPart(value = "file", required = false) @MultipartFileRegex @Valid MultipartFile file,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingHotelService.approveOrReject(request, file, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Report List booking hotel",
            description = "Return list of booking hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "report", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> report(@RequestBody @Valid ReportBookingHotelReqDTO reqDTO,
                             HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(reqDTO.getRequestId(), bookingHotelReportService.getListBookingHotelReport(reqDTO, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Export excel report booking hotel",
            description = "Return file excel of booking hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "exportReport", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> exportBookingHotel (@RequestBody @Valid ReportBookingHotelReqDTO request , HttpServletRequest httpServletRequest) throws IOException {
        return bookingHotelReportService.exportExcelReportBookingHotel(request , httpServletRequest);
    }

    @Operation(summary = "Check hotel ",
            description = "Check if the hotel is in any booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "checkHotelInBooking", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> checkHotelInBooking (@RequestBody @Valid SimpleRequest request , HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingHotelService.checkHotelInBooking(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}
