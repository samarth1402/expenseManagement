package com.adobe.prj.dao;

import com.adobe.prj.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseDao extends JpaRepository<Expense, Integer>, JpaSpecificationExecutor<Expense> {
}
