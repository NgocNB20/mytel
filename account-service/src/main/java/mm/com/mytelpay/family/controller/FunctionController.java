package mm.com.mytelpay.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.function.dto.FunctionDetailDto;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.function.FunctionService;
import mm.com.mytelpay.family.business.function.dto.FunctionAddReqDTO;
import mm.com.mytelpay.family.business.function.dto.FunctionEditReqDTO;
import mm.com.mytelpay.family.business.function.dto.FunctionFilterReqDTO;
import mm.com.mytelpay.family.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Function", description = "APIs for function business")
@RestController
@RequestMapping("function")
public class FunctionController extends BaseController {
    @Autowired
    FunctionService functionService;
    @Operation(summary = "Add new function",
            description = "Add new function")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/add", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> addNewFunction(@Valid @RequestBody FunctionAddReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), functionService.addFunction(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Edit function info",
            description = "Edit function info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/update", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> updateFunction(@Valid @RequestBody FunctionEditReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = generateDefaultResponse(request.getRequestId(), functionService.updateFunction(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get function info",
            description = "Get function info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @GetMapping(value = "/getDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FunctionDetailDto> getDetail(@RequestParam String id, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(functionService.getDetail(id, httpServletRequest));
    }

    @Operation(summary = "Delete function",
            description = "Delete function")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/delete", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> deleteFunction(@Valid @RequestBody SimpleRequest request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), functionService.deleteFunction(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Filter function",
            description = "Filter function")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getList", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> filterFunction(@RequestBody @Valid FunctionFilterReqDTO request, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), functionService.getList(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

}
