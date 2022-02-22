package com.adobe.prj.helper;

import com.adobe.prj.entity.Client;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ClientSpecification {
    public Specification<Client> isDeletedQuery(Boolean isDeleted) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), isDeleted);
    }
}
