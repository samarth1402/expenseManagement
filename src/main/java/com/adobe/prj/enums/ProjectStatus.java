package com.adobe.prj.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum ProjectStatus {

    STARTED("Started"),
    NOT_STARTED("Not Started"),
    DONE("Done");

    private String status;

    ProjectStatus(String status){
        this.status=status;
    }

    @JsonCreator
    public static ProjectStatus decode(final String status) {
        return Stream.of(ProjectStatus.values()).filter(targetEnum -> targetEnum.status.equals(status)).findFirst().orElse(null);
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}
