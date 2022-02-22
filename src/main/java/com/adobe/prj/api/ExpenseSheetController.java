package com.adobe.prj.api;

import com.adobe.prj.dto.ExpenseSheetDTO;
import com.adobe.prj.entity.ExpenseSheet;
import com.adobe.prj.entity.File;
import com.adobe.prj.enums.ExpenseStatus;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.exception.UnauthorizedAccessException;
import com.adobe.prj.service.ExpenseSheetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "Expense Sheet controller")
@RequestMapping("api/expenseSheet")
public class ExpenseSheetController {

    @Autowired
    private ExpenseSheetService service;

    @ApiOperation(value = "Get expense list")
    @GetMapping("/list")
    public @ResponseBody
    List<ExpenseSheet> getExpenseList(@RequestParam(value = "filedBy", required = false) String filedBy,
                                      @RequestParam(value = "startDate", required = false) Long startDate,
                                      @RequestParam(value = "endDate", required = false) Long endDate,
                                      @RequestParam(value = "approver", required = false) String approver,
                                      @RequestParam(value = "status", required = false) String status){
        return service.getExpenseSheetList(filedBy, startDate, endDate, approver,ExpenseStatus.decode(status));
    }

    @ApiOperation(value = "Get expense sheet")
    @GetMapping("/{expenseSheetId}")
    public @ResponseBody ExpenseSheet getExpenseSheet(@PathVariable("expenseSheetId") int expenseSheetId) throws EntityNotFoundException {
        return service.getExpenseSheet(expenseSheetId);
    }

    @ApiOperation(value = "Add expense sheet")
    @PostMapping
    public @ResponseBody ExpenseSheet addExpenseSheet(@RequestBody ExpenseSheetDTO expenseSheetDTO) throws EntityNotFoundException {
        return service.addExpenseSheet(expenseSheetDTO);
    }

    @ApiOperation(value = "Update expense sheet")
    @PutMapping("/{expenseSheetId}")
    public @ResponseBody ExpenseSheet updateExpenseSheet(@PathVariable("expenseSheetId") int expenseSheetId,
                                                         @RequestBody ExpenseSheetDTO expenseSheetDTO) throws EntityNotFoundException, UnauthorizedAccessException {
        return service.updateExpenseSheet(expenseSheetId, expenseSheetDTO);
    }

    @ApiOperation(value = "Delete expense sheet")
    @DeleteMapping("/{expenseSheetId}")
    public @ResponseBody void deleteExpenseSheet(@PathVariable("expenseSheetId") int expenseSheetId) throws EntityNotFoundException {
        service.deleteExpenseSheet(expenseSheetId);
    }

    @ApiOperation(value = "Print expense sheet")
    @GetMapping("/{expenseSheetId}/print")
    public @ResponseBody ResponseEntity<byte[]> printExpenseSheet(@PathVariable("expenseSheetId") int expenseSheetId) throws EntityNotFoundException {
        File pdfFile = service.printExpenseSheet(expenseSheetId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pdfFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfFile.getFileName() + "\"")
                .body(pdfFile.getData());
    }
}
