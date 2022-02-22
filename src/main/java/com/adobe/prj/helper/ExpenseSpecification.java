package com.adobe.prj.helper;

import com.adobe.prj.entity.Expense;
import com.adobe.prj.enums.ExpenseStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ExpenseSpecification {

    public Specification<Expense> expenseSheetIdQuery(int expenseSheetId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("expenseSheet").get("expenseSheetId"), expenseSheetId);
    }

    public Specification<Expense> reimburseQuery(Boolean reimburse) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("reimburse"), reimburse);
    }

    public Specification<Expense> filedByQuery(String filedByName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("filedBy").get("name"), filedByName);
    }

    public Specification<Expense> startDateQuery(long startDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("expenseEntryDate"), startDate);
    }

    public Specification<Expense> endDateQuery(long endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("expenseEntryDate"), endDate);
    }

    public Specification<Expense> statusQuery(ExpenseStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }
}
