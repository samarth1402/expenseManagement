package com.adobe.prj.api;

import com.adobe.prj.TestSecurityConfig;
import com.adobe.prj.dto.ExpenseDTO;
import com.adobe.prj.entity.Expense;
import com.adobe.prj.entity.ExpenseSheet;
import com.adobe.prj.entity.File;
import com.adobe.prj.enums.ExpenseStatus;
import com.adobe.prj.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @Test
    public void addExpenseTest() throws Exception {
        ExpenseSheet expenseSheet = ExpenseSheet.builder().expenseSheetId(1)
                .build();

        List<Expense> expenseList = new ArrayList<>();
        Expense expense1 = Expense.builder().expenseId(1)
                .expenseName("hotel")
                .expenseEntryDate(90232)
                .amount(9000.0)
                .build();
        expenseList.add(expense1);
        expenseSheet.setExpenseList(expenseList);

        when(expenseService.addExpense(Mockito.anyInt() ,Mockito.any(ExpenseDTO.class))).thenReturn(expenseSheet);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(expense1);


                mockMvc.perform(post("/api/expenseSheet/1/expense")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.expenseList[0].expenseName",Matchers.equalTo("hotel")));

        verify(expenseService, times(1)).addExpense(Mockito.anyInt() ,Mockito.any(ExpenseDTO.class));

    }

    @Test
    public void getExpenseByExpenseIdTest() throws Exception {
        ExpenseSheet expenseSheet = ExpenseSheet.builder().expenseSheetId(1)
                .build();

        List<Expense> expenseList = new ArrayList<>();
        Expense expense = Expense.builder().expenseId(1)
                .expenseName("hotel")
                .expenseEntryDate(90232)
                .amount(9000.0)
                .build();
        expenseList.add(expense);
        expenseSheet.setExpenseList(expenseList);

        when(expenseService.getExpenseByExpenseId(Mockito.anyInt())).thenReturn(expense);

        mockMvc.perform(get("/api/expenseSheet/expense/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseName", Matchers.equalTo("hotel")));

        verify(expenseService, times(1)).getExpenseByExpenseId(Mockito.anyInt());

    }

    @Test
    public void updateExpenseTest() throws Exception {
        ExpenseSheet expenseSheet = ExpenseSheet.builder().expenseSheetId(1)
                .build();

        List<Expense> expenseList = new ArrayList<>();
        Expense expense1 = Expense.builder().expenseId(1)
                .expenseName("hotel")
                .expenseEntryDate(90232)
                .amount(9000.0)
                .build();
        expenseList.add(expense1);
        expenseSheet.setExpenseList(expenseList);

        when(expenseService.updateExpense(Mockito.anyInt() ,Mockito.anyInt(), Mockito.any(ExpenseDTO.class)))
                .thenReturn(expenseSheet);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(expense1);


        mockMvc.perform(put("/api/expenseSheet/1/expense/1")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseList[0].expenseName",Matchers.equalTo("hotel")));

        verify(expenseService, times(1)).updateExpense(Mockito.anyInt() ,Mockito.anyInt(), Mockito.any(ExpenseDTO.class));

    }

    @Test
    public void deleteExpenseTest() throws Exception {
        ExpenseSheet expenseSheet = ExpenseSheet.builder().expenseSheetId(1)
                .build();

        when(expenseService.deleteExpense(Mockito.anyInt() ,Mockito.anyInt()))
                .thenReturn(expenseSheet);

        mockMvc.perform(delete("/api/expenseSheet/1/expense/1"))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).deleteExpense(Mockito.anyInt() ,Mockito.anyInt());

    }

    @Test
    public void getExpenseListTest() throws Exception {
        List<Expense> expenseList = new ArrayList<>();
        Expense expense1 = Expense.builder().expenseId(1)
                .expenseName("hotel")
                .expenseEntryDate(90232)
                .amount(9000.0)
                .build();
        expenseList.add(expense1);
        Expense expense2 = Expense.builder().expenseId(1)
                .expenseName("cab")
                .expenseEntryDate(78654)
                .amount(500.0)
                .build();
        expenseList.add(expense2);

        when(expenseService.getExpenseList(Mockito.anyInt(), Mockito.anyString(), Mockito.anyLong() , Mockito.anyLong(), Mockito.any(ExpenseStatus.class), Mockito.anyBoolean()))
                .thenReturn(expenseList);

        mockMvc.perform(get("/api/expenseSheet/expense/list?expenseSheetId=1&filedBy=atul&startDate=901289&endDate=823892&status=Submitted&reimburse=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].expenseName", Matchers.equalTo("hotel")));

        verify(expenseService, times(1)).getExpenseList(Mockito.anyInt(), Mockito.anyString(), Mockito.anyLong() , Mockito.anyLong(), Mockito.any(ExpenseStatus.class), Mockito.anyBoolean());
    }

    @Test
    public void getExpenseSummaryTest() throws Exception {

        List<Double> summary = new ArrayList<>();
        summary.add(8908.0);
        summary.add(7860.0);

        when(expenseService.getExpenseSummary(Mockito.anyString(), Mockito.anyInt())).thenReturn(summary);

        mockMvc.perform(get("/api/expenseSheet/expense/summary/Atul/2021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1]", Matchers.equalTo(7860.0)));

        verify(expenseService, times(1)).getExpenseSummary(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void getFilesTest() throws Exception {

        List<File> fileList = new ArrayList<>();
        File file = File.builder().fileId(1).fileName("pdf").build();
        fileList.add(file);

        when(expenseService.getFiles(Mockito.anyInt())).thenReturn(fileList);

        mockMvc.perform(get("/api/expenseSheet/expense/1/file/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fileName", Matchers.equalTo("pdf")));

        verify(expenseService, times(1)).getFiles(Mockito.anyInt());

    }

    @Test
    public void uploadFileTest() throws Exception {

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        File file1 = File.builder().fileId(1).fileName("hello").build();

        when(expenseService.storeFile(Mockito.anyInt(), Mockito.any(MultipartFile.class))).thenReturn(file1);

        mockMvc.perform(multipart("/api/expenseSheet/expense/1/file/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName",Matchers.equalTo("hello")));

        verify(expenseService, times(1)).storeFile(Mockito.anyInt(), Mockito.any(MultipartFile.class));

    }

    @Test
    public void getFileTest() throws Exception {

        File file = File.builder().fileId(1).fileName("hello")
                .fileType("application/pdf")
                .build();

        when(expenseService.getFile(Mockito.anyInt(), Mockito.anyInt())).thenReturn(file);

        mockMvc.perform(get("/api/expenseSheet/expense/1/file/download/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        verify(expenseService, times(1)).getFile(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void deleteFileTest() throws Exception {

        doNothing().when(expenseService).deleteFile(Mockito.anyInt(), Mockito.anyInt());

        mockMvc.perform(delete("/api/expenseSheet/expense/1/file/delete/1"))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).deleteFile(Mockito.anyInt(), Mockito.anyInt());

    }

}
