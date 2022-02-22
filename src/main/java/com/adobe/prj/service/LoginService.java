package com.adobe.prj.service;

import com.adobe.prj.config.JWTTokenHelper;
import com.adobe.prj.dto.AuthenticationRequest;
import com.adobe.prj.entity.MyUserDetails;
import com.adobe.prj.entity.User;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JWTTokenHelper jWTTokenHelper;

    @Autowired
    private UserService userService;

    public LoginResponse loginUser(AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException, EntityNotFoundException {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUserName(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        MyUserDetails userDetails=(MyUserDetails)authentication.getPrincipal();
        String jwtToken=jWTTokenHelper.generateToken(userDetails.getUsername());

        User user = userService.getUserByEmailId(userDetails.getUsername());
        LoginResponse response=new LoginResponse();
        response.setToken(jwtToken);
        response.setUser(user);
        response.setNewUser(user.isNewUser());
        if(user.isNewUser()==true){
            userService.updateNewUser(user);
        }
        return response;
    }

}
