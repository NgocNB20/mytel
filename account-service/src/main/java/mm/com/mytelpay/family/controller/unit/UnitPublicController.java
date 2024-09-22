package mm.com.mytelpay.family.controller.unit;

import io.swagger.v3.oas.annotations.tags.Tag;
import mm.com.mytelpay.family.business.unit.UnitService;
import mm.com.mytelpay.family.business.unit.dto.UnitFilterResDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "Unit", description = "APIs for Unit service, no need to login")
@RequestMapping("/public/unit/")
@Validated
@RestController
public class UnitPublicController {

    @Autowired
    private UnitService unitService;

    @GetMapping(value = "getAll", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<UnitFilterResDTO>> getAll(HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(unitService.getAll(httpServletRequest));
    }

}
