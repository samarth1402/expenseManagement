package com.adobe.prj.api;
import com.adobe.prj.TestSecurityConfig;
import com.adobe.prj.entity.Client;
import com.adobe.prj.service.ClientService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Test
    public void getClientTest() throws Exception {

        Client client = Client.builder().name("pawan")
                .email("pawan@gmail.com")
                .build();

        Mockito.when(clientService.getClientById("pawan@gmail.com"))
                .thenReturn(client);

        mockMvc.perform(get("/api/client/pawan@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.equalTo("pawan")));

        verify(clientService, times(1)).getClientById(Mockito.anyString());
    }

    @Test
    public void addClientTest() throws Exception {

        Client client = Client.builder().name("pawan")
                .email("pawan@gmail.com")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(client);

        when(clientService.addClient(Mockito.any(Client.class))).thenReturn(client);

        mockMvc.perform(post("/api/client")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.equalTo("pawan")));

        verify(clientService, times(1)).addClient(Mockito.any(Client.class));

    }

    @Test
    public void getClientListTest() throws Exception {
        List<Client> clientList = new ArrayList<>();
        clientList.add(Client.builder().name("pawan").email("pawan@gmail.com").build());
        clientList.add(Client.builder().name("sam").email("sam@gmail.com").build());

        when(clientService.getClientList()).thenReturn(clientList);

        mockMvc.perform(get("/api/client/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("pawan@gmail.com")))
                .andExpect(jsonPath("$[0].name", is("pawan")))
                .andExpect(jsonPath("$[1].name", is("sam")));

        verify(clientService, times(1)).getClientList();
    }

    @Test
    public void modifyClientTest() throws Exception {

        Client client = Client.builder().name("pawan")
                .email("pawan@gmail.com")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(client);

        when(clientService.updateClient(Mockito.anyString(),Mockito.any(Client.class))).thenReturn(client);

        mockMvc.perform(put("/api/client/pawan@gmail.com")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.equalTo("pawan")));

        verify(clientService, times(1)).updateClient(Mockito.anyString(),Mockito.any(Client.class));

    }

    @Test
    public void deleteClientTest() throws Exception {



        doNothing().when(clientService).deleteClient(Mockito.anyString());

        mockMvc.perform(delete("/api/client/pawan@gmail.com"))
                .andExpect(status().isOk());

        verify(clientService, times(1)).deleteClient(Mockito.anyString());
    }

}
