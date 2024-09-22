package mm.com.mytelpay.family.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.roleapprove.RoleApproveService;
import mm.com.mytelpay.family.business.roleapprove.dto.FilterRoleApproveReqDTO;
import mm.com.mytelpay.family.business.roleapprove.dto.FilterRoleApproveResDTO;
import mm.com.mytelpay.family.model.RoleApprove;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "Role", description = "APIs for role business")
@RequestMapping("/roleApprove")
@Validated
@RestController
public class RoleApproveController extends BaseController{

    @Autowired
    RoleApproveService roleApproveService;

    @Operation(summary = "Filter role approve", description = "Filter role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"
                    , content = @Content(schema = @Schema(implementation = FilterRoleApproveResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Input invalid")})
    @PostMapping(value = "/getList", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommonResponseDTO> filterRoleApprove(@Valid @RequestBody FilterRoleApproveReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        List<RoleApprove> result = roleApproveService.filterRoleApprove(reqDTO, httpServletRequest);
        CommonResponseDTO commonResponse = generateDefaultResponse(null , result);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
