package com.adobe.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class AuthenticationRequest {

    private String userName;
    private String password;
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }


}