package com.adobe.prj.helper;

import com.adobe.prj.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {
    public Specification<User> isDeletedQuery(Boolean isDeleted) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), isDeleted);
    }

    public Specification<User> isManagerQuery(Boolean isManager) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isManager"), isManager);
    }
}
