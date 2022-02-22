package com.adobe.prj.api;

import com.adobe.prj.entity.ExpenseSheet;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.service.ExpenseSheetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "Approval controller")
@RequestMapping("api/approval")
public class ApprovalController {

    @Autowired
    private ExpenseSheetService service;

    @ApiOperation(value = "Approve expense sheet")
    @PutMapping("/expenseSheet/{expenseSheetId}")
    public @ResponseBody ExpenseSheet approveExpenseSheet(@PathVariable("expenseSheetId") int expenseSheetId,
                                                          @RequestParam("status")String status) throws EntityNotFoundException {
        return service.approveExpenseSheet(expenseSheetId, status);
    }
}
