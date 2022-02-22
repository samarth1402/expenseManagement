package com.adobe.prj.api;

import com.adobe.prj.TestSecurityConfig;
import com.adobe.prj.entity.*;
import com.adobe.prj.enums.ExpenseStatus;
import com.adobe.prj.enums.ProjectStatus;
import com.adobe.prj.service.ExpenseSheetService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class ApprovalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseSheetService expenseSheetService;

    private static ExpenseSheet expenseSheet;

    @BeforeAll
    public static void setup() {

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
    }

    @Test
    public void approveExpenseSheetTest() throws Exception{
        when(expenseSheetService.approveExpenseSheet(1, "Approved")).thenReturn(expenseSheet);

        mockMvc.perform(put("/api/approval/expenseSheet/1?status=Approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filedBy.emailId", Matchers.equalTo("rahul@gmail.com")))
                .andExpect(jsonPath("$.project.projectName", Matchers.equalTo("bugApp")))
                .andExpect(jsonPath("$.totalAmount", Matchers.equalTo(0.0)));

        verify(expenseSheetService, times(1)).approveExpenseSheet(1, "Approved");
    }
}
