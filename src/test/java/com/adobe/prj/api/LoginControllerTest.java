package com.adobe.prj.api;

import com.adobe.prj.TestSecurityConfig;
import com.adobe.prj.dto.AuthenticationRequest;
import com.adobe.prj.response.LoginResponse;
import com.adobe.prj.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    LoginService loginService;

    @Test
    public void loginTest() throws Exception {

        AuthenticationRequest user = AuthenticationRequest.builder().userName("pawan")
                .password("pass")
                .build();

        LoginResponse response = LoginResponse.builder().token("ik").build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        when(loginService.loginUser(user)).thenReturn(response);

        mockMvc.perform(post("/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(loginService, times(1)).loginUser(Mockito.any(AuthenticationRequest.class));

    }

}
