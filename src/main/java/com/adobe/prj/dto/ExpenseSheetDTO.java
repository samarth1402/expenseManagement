package com.adobe.prj.dto;

import com.adobe.prj.enums.ExpenseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseSheetDTO {
    private String filedBy;
    private long sheetDate;
    private ExpenseStatus status;
    private String currency;
    private String description;
    private  String projectName;
}
