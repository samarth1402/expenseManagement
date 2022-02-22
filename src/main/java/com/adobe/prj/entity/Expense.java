package com.adobe.prj.entity;

import com.adobe.prj.enums.ExpenseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Table(name = "expenses")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Expense {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer expenseId;

    private String expenseName;

    private String paymentMethod;

    private String currency;

    @NotNull(message = "Expense date needs to be added")
    private long expenseEntryDate;

    private Boolean billable;

    private Boolean reimburse;

    @Min(value=1, message = "Net Amount should be at-least {value}")
    private Double netAmount;

    private String taxZone;

    @Min(value=0, message = "Tax cannot be negative")
    private Double tax;

    @Min(value=1, message = "Amount should be at-least {value}")
    private Double amount;

    private String description;

    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "expenseId")
    private List<File> files = new ArrayList<>();

    @NotNull(message = "Expense should be filed by a user")
    @ManyToOne
    @JoinColumn(name="filedBy")
    private User filedBy;

    @ManyToOne()
    @JoinColumn(name = "expenseSheetId", referencedColumnName = "expenseSheetId")
    @JsonIgnoreProperties("expenseList")
    private ExpenseSheet expenseSheet;

}
