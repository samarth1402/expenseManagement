package com.adobe.prj.api;

import com.adobe.prj.TestSecurityConfig;
import com.adobe.prj.dto.ExpenseSheetDTO;
import com.adobe.prj.entity.*;
import com.adobe.prj.enums.ExpenseStatus;
import com.adobe.prj.enums.ProjectStatus;
import com.adobe.prj.service.ExpenseSheetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class ExpenseSheetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseSheetService expenseSheetService;

    private static ExpenseSheetDTO expenseSheetDTO;
    private static ExpenseSheet expenseSheet;
    private static File file;

    @BeforeAll
    public static void setup() {
        expenseSheetDTO = ExpenseSheetDTO.builder()
                .filedBy("Rahul")
                .sheetDate(123455666935L)
                .status(ExpenseStatus.NOT_SUBMITTED)
                .currency("INR")
                .description("description")
                .projectName("bugApp")
                .build();

        User rahul = User.builder()
                .name("Rahul")
                .emailId("rahul@gmail.com")
                .isManager(false)
                .build();

        User rita = User.builder()
                .name("Rita")
                .emailId("rita@gmail.com")
                .isManager(true)
                .build();

        Client adobe = Client.builder()
                .name("Adobe")
                .email("adobe@adobe.com")
                .build();

        Project bugApp = Project.builder()
                .projectCode("PC1")
                .projectName("bugApp")
                .client(adobe)
                .teamLead(rita)
                .projectManager(rita)
                .teamMembers(Arrays.asList(rahul))
                .status(ProjectStatus.STARTED)
                .build();

        expenseSheet = ExpenseSheet.builder()
                .expenseSheetId(1)
                .filedBy(rahul)
                .project(bugApp)
                .sheetDate(123455666935L)
                .status(ExpenseStatus.NOT_SUBMITTED)
                .approver(rita)
                .description("description")
                .currency("INR")
                .build();

        file = File.builder()
                .fileId(1)
                .fileName("Report")
                .fileType(MediaType.APPLICATION_PDF_VALUE)
                .data(null)
                .build();
    }

    @Test
    public void getExpenseListTest() throws Exception {
        Mockito.when(expenseSheetService.getExpenseSheetList("Rohan", null, null, "Rita", null))
                .thenReturn(Arrays.asList(expenseSheet));

        mockMvc.perform(get("/api/expenseSheet/list?filedBy=Rohan&approver=Rita"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filedBy.emailId", Matchers.equalTo("rahul@gmail.com")))
                .andExpect(jsonPath("$[0].project.projectName", Matchers.equalTo("bugApp")))
                .andExpect(jsonPath("$[0].totalAmount", Matchers.equalTo(0.0)));

        verify(expenseSheetService, times(1)).getExpenseSheetList("Rohan", null, null, "Rita", null);
    }

    @Test
    public void getExpenseSheetTest() throws Exception {
        Mockito.when(expenseSheetService.getExpenseSheet(1))
                .thenReturn(expenseSheet);

        mockMvc.perform(get("/api/expenseSheet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filedBy.emailId", Matchers.equalTo("rahul@gmail.com")))
                .andExpect(jsonPath("$.project.projectName", Matchers.equalTo("bugApp")))
                .andExpect(jsonPath("$.totalAmount", Matchers.equalTo(0.0)));

        verify(expenseSheetService, times(1)).getExpenseSheet(1);
    }

    @Test
    public void addExpenseSheetTest() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(expenseSheetDTO);

        when(expenseSheetService.addExpenseSheet(expenseSheetDTO)).thenReturn(expenseSheet);

        mockMvc.perform(post("/api/expenseSheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filedBy.emailId", Matchers.equalTo("rahul@gmail.com")))
                .andExpect(jsonPath("$.project.projectName", Matchers.equalTo("bugApp")))
                .andExpect(jsonPath("$.totalAmount", Matchers.equalTo(0.0)));

        verify(expenseSheetService, times(1)).addExpenseSheet(expenseSheetDTO);
    }

    @Test
    public void updateExpenseSheetTest() throws Exception{
        String jsonRequest = objectMapper.writeValueAsString(expenseSheetDTO);
        when(expenseSheetService.updateExpenseSheet(1, expenseSheetDTO)).thenReturn(expenseSheet);

        mockMvc.perform(put("/api/expenseSheet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filedBy.emailId", Matchers.equalTo("rahul@gmail.com")))
                .andExpect(jsonPath("$.project.projectName", Matchers.equalTo("bugApp")))
                .andExpect(jsonPath("$.totalAmount", Matchers.equalTo(0.0)));

        verify(expenseSheetService, times(1)).updateExpenseSheet(1, expenseSheetDTO);
    }

    @Test
    public void deleteExpenseSheetTest() throws Exception {
        doNothing().when(expenseSheetService).deleteExpenseSheet(Mockito.anyInt());

        mockMvc.perform(delete("/api/expenseSheet/2"))
                .andExpect(status().isOk());

        verify(expenseSheetService, times(1)).deleteExpenseSheet(Mockito.anyInt());
    }

    @Test
    public void printExpenseSheet() throws  Exception {
        when(expenseSheetService.printExpenseSheet(1)).thenReturn(file);

        mockMvc.perform(get("/api/expenseSheet/1/print"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        verify(expenseSheetService, times(1)).printExpenseSheet(1);
    }

}
