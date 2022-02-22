package com.adobe.prj.entity;

import com.adobe.prj.enums.ExpenseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Table(name = "expenseSheet")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int expenseSheetId;

    @NotNull(message = "ExpenseSheet should be filed by a user")
    @ManyToOne
    @JoinColumn(name="filedBy")
    private User filedBy;

    @ManyToOne
    @JoinColumn(name="projectName")
    private Project project;

    @NotNull(message = "SheetDate needs to be added")
    private long sheetDate;

    @Builder.Default
    private Double totalAmount = 0.0;

    @Builder.Default
    private Double reimbursementAmount = 0.0;

    @Builder.Default
    private Double billableAmount = 0.0;

    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;

    private String currency;

    @NotNull(message = "An approver should be selected")
    @ManyToOne
    @JoinColumn(name="approver")
    private User approver;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "expenseSheet", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("expenseSheet")
    private List<Expense> expenseList = new ArrayList<>();

    private String description;

}
