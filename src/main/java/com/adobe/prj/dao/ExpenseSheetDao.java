package com.adobe.prj.dao;

import com.adobe.prj.entity.ExpenseSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseSheetDao extends JpaRepository<ExpenseSheet, Integer>, JpaSpecificationExecutor<ExpenseSheet> {
}
