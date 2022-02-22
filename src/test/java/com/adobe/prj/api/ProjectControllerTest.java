package com.adobe.prj.api;

import com.adobe.prj.TestSecurityConfig;
import com.adobe.prj.dto.ProjectDTO;
import com.adobe.prj.entity.Client;
import com.adobe.prj.entity.Project;
import com.adobe.prj.entity.User;
//import com.adobe.prj.service.UserService;
import com.adobe.prj.service.ProjectService;
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

import static com.adobe.prj.enums.ProjectStatus.DONE;
import static com.adobe.prj.enums.ProjectStatus.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;


@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getProjectByProjectCode() throws Exception {
        Project project = Project.builder().projectCode("PC101")
                .projectName("DC")
                .build();

        Mockito.when(projectService.getProjectByProjectCode("PC101"))
                .thenReturn(project);

        mockMvc.perform(get("/api/project/PC101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName", Matchers.equalTo("DC")));

        verify(projectService, times(1)).getProjectByProjectCode(Mockito.anyString());
    }

    @Test
    public void addProjectTest() throws Exception {

        ProjectDTO projectDTO = ProjectDTO.builder().projectCode("PC101")
                .projectName("DC")
                .build();
        Project project = Project.builder().projectCode("PC101")
                .projectName("DC")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(projectDTO);

        when(projectService.addProject(Mockito.any(ProjectDTO.class))).thenReturn(project);

        mockMvc.perform(post("/api/project")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectName", Matchers.equalTo("DC")));

        verify(projectService, times(1)).addProject(Mockito.any(ProjectDTO.class));

    }

    @Test
    public void updateProjectTest() throws Exception{
        User manager = User.builder().name("Amrita").emailId("amrita@yahoo.com").build();
        User teamLead = User.builder().name("Meena").emailId("meena@yahoo.com").build();
        Client client = Client.builder().name("pawan").email("pawan@gmail.com").build();
        ProjectDTO projectDTO = ProjectDTO.builder().projectCode("PC101")
                .projectName("DC")
                .status(DONE)
                .build();
        Project project = Project.builder().projectCode("PC101")
                .projectName("DC")
                .teamLead(teamLead)
                .projectManager(manager)
                .client(client)
                .status(STARTED)
                .build();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(projectDTO);
        when(projectService.updateProject(Mockito.anyString(),Mockito.any(ProjectDTO.class))).thenReturn(project);
        mockMvc.perform(put("/api/project/PC101")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.status", Matchers.equalTo(DONE)));
                .andExpect(jsonPath("$.projectName", Matchers.equalTo("DC")));

        verify(projectService, times(1)).updateProject(Mockito.anyString(),Mockito.any(ProjectDTO.class));

    }

    @Test
    public void getProjectsTest() throws Exception {

        User manager = User.builder().name("Amrita").emailId("amrita@yahoo.com").build();
        User teamLead = User.builder().name("Meena").emailId("meena@yahoo.com").build();
        Client client = Client.builder().name("pawan").email("pawan@gmail.com").build();

        List<User> userList1 = new ArrayList<>();
        userList1.add(User.builder().name("Tina").emailId("tina@yahoo.com").build());
        userList1.add(User.builder().name("Mayank").emailId("Mayank@gmail.com").build());

        List<User> userList2 = new ArrayList<>();
        userList2.add(User.builder().name("Tina").emailId("tina@yahoo.com").build());
        userList2.add(User.builder().name("Gauri").emailId("gauri@gmail.com").build());

        List<Project> projectList = new ArrayList<>();
        projectList.add(Project.builder().projectCode("PC101").projectName("DC").teamLead(teamLead).projectManager(manager).client(client).teamMembers(userList1).build());
        projectList.add(Project.builder().projectCode("PC102").projectName("DX").teamLead(teamLead).projectManager(manager).client(client).teamMembers(userList2).build());

        when(projectService.getProjectList("tina@yahoo.com")).thenReturn(projectList);

        mockMvc.perform(get("/api/project/list?emailId=tina@yahoo.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].projectCode", is("PC101")))
                .andExpect(jsonPath("$[0].projectName", is("DC")))
                .andExpect(jsonPath("$[1].projectCode", is("PC102")))
                .andExpect(jsonPath("$[1].projectName", is("DX")));
        verify(projectService, times(1)).getProjectList(Mockito.anyString());
    }

    @Test
    public void deleteProjectByIdTest() throws Exception {

        doNothing().when(projectService).deleteProjectById(Mockito.anyString());

        mockMvc.perform(delete("/api/project/PC101"))
                .andExpect(status().isOk());

        verify(projectService, times(1)).deleteProjectById(Mockito.anyString());
    }



}
