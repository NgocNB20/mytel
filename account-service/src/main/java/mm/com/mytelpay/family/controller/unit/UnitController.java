package mm.com.mytelpay.family.controller.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.unit.UnitService;
import mm.com.mytelpay.family.business.unit.dto.*;
import mm.com.mytelpay.family.controller.BaseController;
import mm.com.mytelpay.family.exception.CommonException;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Unit", description = "APIs for Services")
@RequestMapping("unit/")
@Validated
@RestController
public class UnitController extends BaseController {


    @Autowired
    private UnitService unitService;

    @Operation(summary = "Get List units",
            description = "Return list of units")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UnitFilterResDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getList", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<CommonResponseDTO> filter(
            @RequestBody @Valid UnitFilterReqDTO request,
            HttpServletRequest httpServletRequest
    ) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), unitService.filter(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Get detail of unit",
            description = "Return detail of unit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UnitDetailResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "getDetail", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getDetail(
            @RequestBody @Valid SimpleRequest request,
            HttpServletRequest httpServletRequest
    ) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), unitService.getDetail(request, httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Create Unit",
            description = "Returns unit info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UnitResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "add", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> create(
            @RequestBody @Valid UnitCreateReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        if (unitService.create(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Edit Unit",
            description = "Returns unit info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UnitResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "update", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> edit(
            @RequestBody @Valid UnitEditReqDTO request,
            HttpServletRequest httpServletRequest
    ) throws JsonProcessingException {
        if (unitService.edit(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @Operation(summary = "Delete Unit",
            description = "Returns unit info")
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
    ) {
        if (unitService.delete(request, httpServletRequest)) {
            CommonResponseDTO commonResponse = Util.generateDefaultResponse(request.getRequestId(), null);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } else {
            throw new BusinessEx(request.getRequestId(), CommonException.Business.SYSTEM_BUSY, null);
        }
    }

    @PostMapping("/checkUnit")
    public ResponseEntity<CommonResponseDTO> checkUnitForCanteen (@RequestBody UnitForCanteenReqDTO reqDTO){
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(reqDTO.getRequestId(), unitService.isUnitIdExisted(reqDTO));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping("/getListUnit")
    public ResponseEntity<CommonResponseDTO> getListUnit(@RequestBody UnitInfoReqDTO reqDTO) {
        CommonResponseDTO commonResponseDTO = Util.generateDefaultResponse(reqDTO.getRequestId(), unitService.getListUnit(reqDTO));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponseDTO);
    }

    @PostMapping("/getUnitForChef")
    public ResponseEntity<CommonResponseDTO> getUnitForChef(@RequestBody UnitForChefReqDTO reqDTO){
        CommonResponseDTO commonResponseDTO = Util.generateDefaultResponse(reqDTO.getRequestId(), unitService.getUnitForChef(reqDTO));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponseDTO);
    }
}
