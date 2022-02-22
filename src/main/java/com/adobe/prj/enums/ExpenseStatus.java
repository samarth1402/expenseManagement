package com.adobe.prj.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum ExpenseStatus {

    NOT_SUBMITTED("Not Submitted"),
    SUBMITTED("Submitted"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private String status;

    ExpenseStatus(String status){
        this.status=status;
    }

    @JsonCreator
    public static ExpenseStatus decode(final String status) {
        return Stream.of(ExpenseStatus.values()).filter(targetEnum -> targetEnum.status.equals(status)).findFirst().orElse(null);
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}
