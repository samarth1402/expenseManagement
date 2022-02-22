package com.adobe.prj.api;

import com.adobe.prj.dto.ExpenseDTO;
import com.adobe.prj.entity.Expense;
import com.adobe.prj.entity.ExpenseSheet;
import com.adobe.prj.entity.File;
import com.adobe.prj.enums.ExpenseStatus;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.service.ExpenseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@Api(value = "Expense controller")
@RequestMapping("api/expenseSheet")
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    @ApiOperation(value = "Add an expense")
    @PostMapping("/{expenseSheetId}/expense")
    @ResponseStatus(code = HttpStatus.CREATED)
    public @ResponseBody
    ExpenseSheet addExpense(@PathVariable("expenseSheetId") int expenseSheetId, @RequestBody ExpenseDTO expense)
            throws EntityNotFoundException {
        return service.addExpense(expenseSheetId, expense);
    }

    @ApiOperation(value = "Get expense by id")
    @GetMapping("/expense/{expenseId}")
    public @ResponseBody Expense getExpenseByExpenseId(@PathVariable("expenseId") int expenseId) throws EntityNotFoundException {
        return service.getExpenseByExpenseId(expenseId);
    }

    @ApiOperation(value = "Update expense")
    @PutMapping("/{expenseSheetId}/expense/{expenseId}")
    public @ResponseBody ExpenseSheet updateExpense(@PathVariable("expenseSheetId") int expenseSheetId,
                                               @PathVariable("expenseId") int expenseId,
                                               @RequestBody ExpenseDTO expenseDTO) throws EntityNotFoundException {
        return service.updateExpense(expenseSheetId, expenseId, expenseDTO);
    }

    @ApiOperation(value = "Delete a expense")
    @DeleteMapping("/{expenseSheetId}/expense/{expenseId}")
    public @ResponseBody ExpenseSheet deleteExpense(@PathVariable("expenseSheetId") int expenseSheetId,
                                            @PathVariable("expenseId") int expenseId) throws EntityNotFoundException {
        return service.deleteExpense(expenseSheetId, expenseId);
    }

    @ApiOperation(value = "Get expense list")
    @GetMapping("/expense/list")
    public @ResponseBody
    List<Expense> getExpenseList(@RequestParam(value = "expenseSheetId", required = false) Integer expenseSheetId,
                                  @RequestParam(value = "filedBy", required = false) String filedBy,
                                  @RequestParam(value = "startDate", required = false) Long startDate,
                                  @RequestParam(value = "endDate", required = false) Long endDate,
                                  @RequestParam(value = "status", required = false) String status,
                                  @RequestParam(value = "reimburse", required = false) Boolean reimburse) {
        return service.getExpenseList(expenseSheetId, filedBy, startDate, endDate, ExpenseStatus.decode(status), reimburse);
    }

    @ApiOperation(value = "Get expense summary")
    @GetMapping("/expense/summary/{filedBy}/{year}")
    public @ResponseBody List<Double> getExpenseSummary(@PathVariable(value = "filedBy") String filedBy,
                                                      @PathVariable(value = "year") int year) throws ParseException {
        return service.getExpenseSummary(filedBy, year);
    }

    @ApiOperation(value = "Get attachements list")
    @GetMapping("/expense/{expenseId}/file/list")
    public List<File> getFiles(@PathVariable("expenseId") int expenseId ) throws EntityNotFoundException {
        return service.getFiles(expenseId);
    }

    @ApiOperation(value = "Upload a file")
    @PostMapping("/expense/{expenseId}/file/upload")
    public File uploadFile(@PathVariable("expenseId") int expenseId, @RequestParam("file") MultipartFile file) throws EntityNotFoundException, IOException {
       return service.storeFile(expenseId, file);
    }

    @ApiOperation(value = "Get a file")
    @GetMapping("/expense/{expenseId}/file/download/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable("expenseId") int expenseId, @PathVariable("fileId") int fileId) throws EntityNotFoundException, IOException {
        File file = service.getFile(expenseId, fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getData());
    }

    @ApiOperation(value = "Delete a file")
    @DeleteMapping("expense/{expenseId}/file/delete/{fileId}")
    public void deleteFile(@PathVariable("expenseId") int expenseId, @PathVariable("fileId") int fileId) throws EntityNotFoundException {
        service.deleteFile(expenseId, fileId);
    }

}
