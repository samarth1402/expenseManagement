package com.adobe.prj.helper;

import com.adobe.prj.entity.Project;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProjectSpecification {
    public Specification<Project> isDeletedQuery(Boolean isDeleted) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), isDeleted);
    }
}
