package com.adobe.prj.api;

import com.adobe.prj.TestSecurityConfig;
import com.adobe.prj.dto.ResetPasswordDTO;
import com.adobe.prj.entity.User;
import com.adobe.prj.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void getUserByEmailIdTest() throws Exception {
        User user = User.builder().name("Raval")
                .emailId("raval@gmail.com")
                .build();

        when(userService.getUserByEmailId("raval@gmail.com"))
                .thenReturn(user);

        mockMvc.perform(get("/api/user/raval@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.equalTo("Raval")));

        verify(userService, times(1)).getUserByEmailId(Mockito.anyString());
    }

    @Test
    public void addUserTest() throws Exception {

        User user = User.builder().name("Raval")
                .emailId("raval@gmail.com")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        when(userService.addUser(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/user")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.equalTo("Raval")));

        verify(userService, times(1)).addUser(Mockito.any(User.class));

    }

    @Test
    public void getUserListTest() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(User.builder().name("Tina").emailId("tina@yahoo.com").isManager(true).build());
        userList.add(User.builder().name("Mayank").emailId("Mayank@gmail.com").build());

       when(userService.getUserList(true)).thenReturn(userList);

       mockMvc.perform(get("/api/user/list?isManager=true"))
                .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].emailId", is("tina@yahoo.com")))
                .andExpect(jsonPath("$[0].name", is("Tina")))
                .andExpect(jsonPath("$[1].emailId", is("Mayank@gmail.com")))
                .andExpect(jsonPath("$[1].name", is("Mayank")));

        verify(userService, times(1)).getUserList(Mockito.anyBoolean());
    }

    @Test
    public void modifyUserTest() throws Exception {

        ResetPasswordDTO  passwordDTO = ResetPasswordDTO.builder()
                .oldPassword("word")
                .emailId("depa@gmail.com")
                .newPassword("password")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(passwordDTO);

        doNothing().when(userService).updateUser(passwordDTO);

        mockMvc.perform(put("/api/user")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService,times(1)).updateUser(Mockito.any(ResetPasswordDTO.class));

    }

}

