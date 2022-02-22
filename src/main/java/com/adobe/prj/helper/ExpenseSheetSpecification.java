package com.adobe.prj.helper;

import com.adobe.prj.entity.ExpenseSheet;
import com.adobe.prj.enums.ExpenseStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ExpenseSheetSpecification {

    public Specification<ExpenseSheet> filedByQuery(String filedByName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("filedBy").get("name"), filedByName);
    }

    public Specification<ExpenseSheet> startDateQuery(long startDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.gt(root.get("expenseEntryDate"), startDate);
    }

    public Specification<ExpenseSheet> endDateQuery(long endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lt(root.get("expenseEntryDate"), endDate);
    }

    public Specification<ExpenseSheet> approverQuery(String approverName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("approver").get("name"), approverName);
    }

    public Specification<ExpenseSheet> statusQuery(ExpenseStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public Specification<ExpenseSheet> isDeletedQuery(Boolean isDeleted) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), isDeleted);
    }

}
