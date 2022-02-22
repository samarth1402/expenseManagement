package com.adobe.prj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseDTO {
    String expenseName;
    String paymentMethod;
    String currency;
    long expenseEntryDate;
    boolean billable;
    boolean reimburse;
    double netAmount;
    String taxZone;
    double tax;
    double amount;
    String description;
}
