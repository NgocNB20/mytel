package mm.com.mytelpay.family.controller;

import mm.com.mytelpay.family.business.auth.AuthService;
import mm.com.mytelpay.family.models.CallAuthServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/public/auth")
public class AuthController extends BaseController {
    @Autowired
    AuthService authService;

    @PostMapping(value = "/getPermission", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CallAuthServiceDTO> getFunctionFromJwt(@RequestBody String bearToken, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(authService.getFunctionFromJwt(bearToken));
    }

    @PostMapping(value = "/checkPermission", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> checkPermission(@RequestBody String requestUri, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(authService.checkPermission(requestUri, httpServletRequest));
    }

    @PostMapping(value = "/getCurrentAccountId", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getCurrentAccountId(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(authService.getCurrentAccountId(httpServletRequest));
    }

}
