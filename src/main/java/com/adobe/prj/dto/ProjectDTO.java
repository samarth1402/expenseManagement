package com.adobe.prj.dto;

import com.adobe.prj.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDTO {

    private String projectName;

    private String clientName;

    private String projectCode;

    private String teamLead;

    private String projectManager;

    private List<String> teamMembers;

    private String timesheetApprovalType;
    private String expenseApprovalType;
    private String projectDescription;
    private int duration;
    private double cost;
    private ProjectStatus status;
    private String projectType;
    private long startDate;
    private long dueDate;
}
