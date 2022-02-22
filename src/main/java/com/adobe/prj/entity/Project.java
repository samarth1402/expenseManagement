package com.adobe.prj.entity;

import com.adobe.prj.enums.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Table(name = "projects")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @NotBlank(message="ProjectCode cannot be null")
    @Id
    private String projectCode;

    @NotBlank(message="ProjectName cannot be null")
    @Column(unique = true)
    private String projectName;

    @NotNull(message = "ClientName cannot be null")
    @ManyToOne
    @JoinColumn(name="clientEmailId")
    private Client client;

    @NotNull(message = "TeamLead cannot be null")
    @ManyToOne
    @JoinColumn(name="teamLead")
    private User teamLead;

    @NotNull(message = "ProjectManager cannot be null")
    @ManyToOne
    @JoinColumn(name="projectManager")
    private User projectManager;

    @Builder.Default
    @ManyToMany()
    @JoinTable(
            name = "Project_User",
            joinColumns = { @JoinColumn(name = "projectCode") },
            inverseJoinColumns = { @JoinColumn(name = "userEmailId") }
    )
    private List<User> teamMembers = new ArrayList<>();

    private String timesheetApprovalType;
    private String expenseApprovalType;
    private String projectDescription;
    private int duration;
    private double cost;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    private String projectType;
    private long startDate;
    private long dueDate;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private double totalExpense = 0.0;

    @Builder.Default
    private double billableAmount = 0.0;

    @Builder.Default
    @JsonIgnore
    private boolean isDeleted = false;
}
