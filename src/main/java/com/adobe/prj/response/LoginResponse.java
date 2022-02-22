package com.adobe.prj.response;

import com.adobe.prj.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String token;

    private User user;

    private boolean isNewUser;

}