package mm.com.mytelpay.family.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.bookingmeal.BookingMealReportService;
import mm.com.mytelpay.family.business.bookingmeal.BookingMealService;
import mm.com.mytelpay.family.business.bookingmeal.dto.*;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Tag(name = "Meal", description = "APIs for Services")
@RequestMapping("/bookingMeal/")
@Validated
@RestController
public class BookingMealController extends BaseController {

    @Autowired
    private BookingMealService bookingMealService;

    @Autowired
    private BookingMealReportService bookingMealReportService;

    @Operation(summary = "Get List booking meals",
            description = "Return list of booking meals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "filterOrderMeal", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> filter(
            @RequestBody @Valid ReportBookingMealReqDTO reqDTO,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(reqDTO.getRequestId(), bookingMealService.filter(reqDTO, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Create Booking Meal Request",
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
            @RequestBody @Valid BookingMealCreateReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        if (bookingMealService.create(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Get List booking meals",
            description = "Return list of booking meals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "filterForEU", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> filterForEu(
            @RequestBody @Valid FilterBookingMealEUReqDTO reqDTO,
            HttpServletRequest httpServletRequest
    ) {
        CommonResponseDTO commonResponse = generateDefaultResponse(reqDTO.getRequestId(), bookingMealService.getListForEU(reqDTO, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get detail Booking Meal Request",
            description = "Returns booking info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "detailMeal",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> getDetail(
            @RequestBody @Valid BookingMealDetailReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingMealService.getDetail(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Report List booking meals",
            description = "Return list of booking meals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "report", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> report(@RequestBody @Valid ReportBookingMealReqDTO reqDTO,
            HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(reqDTO.getRequestId() ,bookingMealReportService.getListBookingMealReport(reqDTO, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "Export excel report booking meals",
            description = "Return file excel of booking meals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "exportReport", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> exportBookingMeal (@RequestBody @Valid ReportBookingMealReqDTO request , HttpServletRequest httpServletRequest) throws IOException {
        return bookingMealReportService.exportExcelReportBookingMeal(request , httpServletRequest);
    }

    @Operation(summary = "Report fee List booking meals",
            description = "Return list of booking meals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "reportFee", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> reportFee(@RequestBody @Valid ReportFeeBookingMealReqDTO reqDTO,
                             HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(reqDTO.getRequestId() , bookingMealReportService.getListBookingFeeMealReport(reqDTO, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "Export fee excel report booking meals",
            description = "Return file excel of fee booking meals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "exportReportFee", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> exportFeeBookingMeal (@RequestBody @Valid ReportFeeBookingMealReqDTO request , HttpServletRequest httpServletRequest) throws IOException {
        return bookingMealReportService.exportExcelReportFeeBookingMeal(request , httpServletRequest);
    }

    @Operation(summary = "Verify QR booking meal for role Chef",
            description = "Verify QR booking meal for role Chef")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/verifyQr")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> verifyQrBookingMeal(
            @Valid @RequestBody VerifyQrBookingMealReqDTO request,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingMealService.verifyQrBookingMeal(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Cancel booking meal",
            description = "Cancel booking meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> cancelBookingMeal(
            @Valid @RequestBody CancelOrderMealReqDTO request,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), bookingMealService.cancelOrderMeal(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @PostMapping("/getListTotalOrderMeal")
    ResponseEntity<CommonResponseDTO> getTotalBookingMeal(@RequestBody BookingMealFilterReqDTO reqDTO){
        CommonResponseDTO commonResponse = generateDefaultResponse(reqDTO.getRequestId(), bookingMealService.getTotalBookingMeal(reqDTO));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping("/getListBookingMeal")
    ResponseEntity<CommonResponseDTO> getListBookingMeal(@RequestBody BookingMealViewListReqDTO reqDTO){
        CommonResponseDTO commonResponseDTO = generateDefaultResponse(reqDTO.getRequestId(), bookingMealService.listOrderBookingMeal(reqDTO));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponseDTO);
    }
}
