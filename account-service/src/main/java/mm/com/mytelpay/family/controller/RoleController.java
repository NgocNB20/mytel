package mm.com.mytelpay.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import mm.com.mytelpay.family.business.role.RoleService;
import mm.com.mytelpay.family.business.role.dto.*;
import mm.com.mytelpay.family.business.rolehasfunction.RoleHasFunctionService;
import mm.com.mytelpay.family.business.rolehasfunction.dto.PerRoleDTO;
import mm.com.mytelpay.family.logging.RequestUtils;
import mm.com.mytelpay.family.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "Role", description = "APIs for role business")
@RequestMapping("/role")
@Validated
@RestController
public class RoleController {

    @Autowired
    RoleService roleService;

    @Autowired
    RoleHasFunctionService roleHasFunctionService;

    @Operation(summary = "Add new role",
            description = "Add new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/add", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> addNewRole(@RequestBody @Valid AddRoleReqDTO request , HttpServletRequest httpServletRequest){
        roleService.addRole(request);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(RequestUtils.currentRequestId(), null );
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Delete role",
            description = "Delete role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/delete", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> deleteRole(@RequestBody @Valid SimpleRequest request , HttpServletRequest httpServletRequest) {
        roleService.deleteRole(request);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(RequestUtils.currentRequestId(), null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "Filter role", description = "Filter role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"
                    , content = @Content(schema = @Schema(implementation = FilterRoleResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid")})
    @PostMapping(value = "/getList", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public CommonResponseDTO filterRole(@Valid @RequestBody FilterRoleReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        Page<FilterRoleResDTO> result = roleService.getList(reqDTO);
        return Util.generateDefaultResponse(RequestUtils.currentRequestId(), result);
    }

    @Operation(summary = "Permission role information",
            description = "Permission role information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/perRole", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> perRole(@RequestBody PerRoleDTO functionIdDTO, HttpServletRequest httpServletRequest) {
        roleHasFunctionService.perRole(functionIdDTO , httpServletRequest);
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(RequestUtils.currentRequestId(), null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "Get Info Per Role",
            description = "Get Info Per Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommonResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Input invalid", content = @Content)})
    @PostMapping(value = "/getInfoPerRole" , produces = {"application/json"} )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> getInfoPerRole(@Valid @RequestBody SimpleIdRole simpleIdRole, HttpServletRequest httpServletRequest) {
        CommonResponseDTO commonResponse = Util.generateDefaultResponse(RequestUtils.currentRequestId(), roleHasFunctionService.getInfoPerRole(simpleIdRole , httpServletRequest));
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping(value = "getAll", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<FilterRoleResDTO>> getAll(HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getAll());
    }

}
