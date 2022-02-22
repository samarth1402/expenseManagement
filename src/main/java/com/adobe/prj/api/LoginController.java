package com.adobe.prj.api;
import com.adobe.prj.dto.AuthenticationRequest;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.response.LoginResponse;
import com.adobe.prj.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@Api(value = "Login controller")
public class LoginController {

    @Autowired
    LoginService service;

    @ApiOperation(value = "login request")
    @PostMapping("/login")
    public @ResponseBody LoginResponse login(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException, EntityNotFoundException {
        return service.loginUser(authenticationRequest);
    }

}
